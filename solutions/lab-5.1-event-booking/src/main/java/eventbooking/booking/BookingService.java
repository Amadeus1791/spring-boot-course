package eventbooking.booking;

import eventbooking.event.Event;
import eventbooking.event.EventNotFoundException;
import eventbooking.event.EventRepository;
import eventbooking.payment.PaymentClient;
import eventbooking.payment.PaymentDeclinedException;
import eventbooking.payment.PaymentDetails;
import eventbooking.payment.PaymentResponse;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);

    private final BookingRepository bookings;
    private final EventRepository events;
    private final PaymentClient payments;
    private final BookingProperties properties;
    private final Counter confirmed;
    private final Counter declined;
    // Done early: one meter, one series per reason
    private final Counter tooManySeats;
    private final Counter noSeats;
    private final Counter rejectedDeclined;

    public BookingService(BookingRepository bookings, EventRepository events, PaymentClient payments,
                          BookingProperties properties, MeterRegistry registry) {
        this.bookings = bookings;
        this.events = events;
        this.payments = payments;
        this.properties = properties;
        this.confirmed = Counter.builder("bookings.confirmed")
                .description("Bookings that were paid and saved")
                .register(registry);
        this.declined = Counter.builder("bookings.declined")
                .description("Bookings whose payment was declined")
                .register(registry);
        this.tooManySeats = rejected(registry, "too-many-seats");
        this.noSeats = rejected(registry, "no-seats");
        this.rejectedDeclined = rejected(registry, "declined");
    }

    private static Counter rejected(MeterRegistry registry, String reason) {
        return Counter.builder("bookings.rejected")
                .description("Bookings that were not made")
                .tag("reason", reason)
                .register(registry);
    }

    // No transaction here: the payment call must not
    // hold a database connection.
    // Each repository call runs in its own short transaction.
    public BookingResponse book(Long eventId, BookRequest request, String customerEmail) {
        if (request.seats() > properties.maxSeats()) {
            tooManySeats.increment();
            throw new TooManySeatsException(properties.maxSeats());
        }
        Event event = events.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));
        long freeSeats = event.getCapacity() - bookings.countBookedSeats(eventId);
        if (request.seats() > freeSeats) {
            noSeats.increment();
            throw new SeatsNotAvailableException(eventId, freeSeats);
        }
        BigDecimal amount = event.getPrice().multiply(BigDecimal.valueOf(request.seats()));
        PaymentResponse payment = payments.pay(
                "event " + eventId + ", " + customerEmail, amount);
        // Declined is an answer, not an error: no booking.
        if (!"APPROVED".equals(payment.status())) {
            declined.increment();
            rejectedDeclined.increment();
            throw new PaymentDeclinedException(payment.paymentId());
        }
        Booking booking = new Booking(event, customerEmail, request.seats(),
                LocalDateTime.now(), payment.paymentId());
        Booking saved = bookings.save(booking);
        confirmed.increment();
        log.info("Booked {} seats for event {}, booking {}", request.seats(), eventId, saved.getId());
        return BookingResponse.from(saved);
    }

    @Transactional(readOnly = true)
    @PostAuthorize("returnObject.customerEmail() == authentication.name or hasRole('ORGANIZER')")
    public BookingResponse findById(Long id) {
        Booking booking = bookings.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));
        return BookingResponse.from(booking);
    }

    @Transactional(readOnly = true)
    public Page<BookingResponse> findByCustomer(String email, Pageable pageable) {
        return bookings.findByCustomerEmail(email, pageable).map(BookingResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<BookingResponse> findByEvent(Long eventId, Pageable pageable) {
        if (!events.existsById(eventId)) {
            throw new EventNotFoundException(eventId);
        }
        return bookings.findByEventId(eventId, pageable).map(BookingResponse::from);
    }

    @PreAuthorize("hasRole('ORGANIZER') or @bookingAccess.isOwner(#bookingId, authentication.name)")
    public PaymentDetails paymentOf(Long bookingId) {
        Booking booking = bookings.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));
        return payments.find(booking.getPaymentId());
    }
}
