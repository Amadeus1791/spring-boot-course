package eventbooking.booking;

import eventbooking.event.Event;
import eventbooking.event.EventNotFoundException;
import eventbooking.event.EventRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class BookingService {

    private final BookingRepository bookings;
    private final EventRepository events;

    public BookingService(BookingRepository bookings, EventRepository events) {
        this.bookings = bookings;
        this.events = events;
    }

    @Transactional
    public BookingResponse book(Long eventId, BookRequest request) {
        Event event = events.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));
        long freeSeats = event.getCapacity() - bookings.countBookedSeats(eventId);
        if (request.seats() > freeSeats) {
            throw new SeatsNotAvailableException(eventId, freeSeats);
        }
        Booking booking = new Booking(event, request.customerEmail(), request.seats(),
                LocalDateTime.now());
        return BookingResponse.from(bookings.save(booking));
    }

    @Transactional(readOnly = true)
    public BookingResponse findById(Long id) {
        Booking booking = bookings.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));
        return BookingResponse.from(booking);
    }

    @Transactional(readOnly = true)
    public Page<BookingResponse> findByCustomer(String email, Pageable pageable) {
        return bookings.findByCustomerEmail(email, pageable).map(BookingResponse::from);
    }
}
