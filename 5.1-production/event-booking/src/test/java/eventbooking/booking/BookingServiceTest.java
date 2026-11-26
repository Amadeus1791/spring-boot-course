package eventbooking.booking;

import eventbooking.event.Event;
import eventbooking.event.EventRepository;
import eventbooking.payment.PaymentClient;
import eventbooking.payment.PaymentResponse;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// The seat rule without Spring: three mocks, no context, no database.
class BookingServiceTest {

    private final BookingRepository bookings = mock(BookingRepository.class);
    private final EventRepository events = mock(EventRepository.class);
    private final PaymentClient payments = mock(PaymentClient.class);
    private final BookingProperties properties = new BookingProperties(6);
    private final BookingService service = new BookingService(bookings, events, payments, properties,
            new SimpleMeterRegistry());

    private final Event jazzNight = new Event("Jazz Night", "Vienna",
            LocalDateTime.of(2027, 3, 12, 20, 0), 10, new BigDecimal("39.00"), "olivia@example.com");

    @Test
    void refusesMoreSeatsThanAreFree() {
        // Arrange
        when(events.findById(1L)).thenReturn(Optional.of(jazzNight));
        when(bookings.countBookedSeats(1L)).thenReturn(8L);

        // Act
        Throwable thrown = catchThrowable(
                () -> service.book(1L, new BookRequest(3), "alice@example.com"));

        // Assert
        assertThat(thrown).isInstanceOf(SeatsNotAvailableException.class)
                .hasMessage("Event 1 has only 2 free seat(s)");
        verify(payments, never()).pay(any(), any());
        verify(bookings, never()).save(any());
    }

    @Test
    void paysThePriceOfAllSeatsAndSaves() {
        // Arrange
        when(events.findById(1L)).thenReturn(Optional.of(jazzNight));
        when(bookings.countBookedSeats(1L)).thenReturn(0L);
        when(payments.pay(any(), any())).thenReturn(new PaymentResponse("p-1", "APPROVED"));
        when(bookings.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        BookingResponse booked = service.book(1L, new BookRequest(2), "alice@example.com");

        // Assert
        assertThat(booked.paymentId()).isEqualTo("p-1");
        verify(payments).pay("event 1, alice@example.com", new BigDecimal("78.00"));
    }

    @Test
    void refusesMoreThanTheMaximumBeforeAnythingElse() {
        // Act
        Throwable thrown = catchThrowable(
                () -> service.book(1L, new BookRequest(7), "alice@example.com"));

        // Assert
        assertThat(thrown).isInstanceOf(TooManySeatsException.class)
                .hasMessage("At most 6 seats per booking");
        verify(payments, never()).pay(any(), any());
    }
}
