package eventbooking.booking;

import eventbooking.event.Event;
import eventbooking.event.EventRepository;
import eventbooking.payment.PaymentClient;
import eventbooking.payment.PaymentFailedException;
import eventbooking.payment.PaymentResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

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
    private final BookingService service = new BookingService(bookings, events, payments);

    private final Event jazzNight = new Event("Jazz Night", "Vienna",
            LocalDateTime.of(2027, 3, 12, 20, 0), 10, new BigDecimal("39.00"));

    @Test
    void refusesMoreSeatsThanAreFree() {
        // Arrange
        when(events.findById(1L)).thenReturn(Optional.of(jazzNight));
        when(bookings.countBookedSeats(1L)).thenReturn(8L);

        // Act
        Throwable thrown = catchThrowable(
                () -> service.book(1L, new BookRequest("alice@example.com", 3)));

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
        BookingResponse booked = service.book(1L, new BookRequest("alice@example.com", 2));

        // Assert
        assertThat(booked.paymentId()).isEqualTo("p-1");
        verify(payments).pay("event 1, alice@example.com", new BigDecimal("78.00"));
    }

    @Test
    void booksTheLastTwoSeatsAndPays() {
        // Arrange
        when(events.findById(1L)).thenReturn(Optional.of(jazzNight));
        when(bookings.countBookedSeats(1L)).thenReturn(8L);
        when(payments.pay(any(), any())).thenReturn(new PaymentResponse("p-9", "APPROVED"));
        when(bookings.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        BookingResponse booked = service.book(1L, new BookRequest("alice@example.com", 2));

        // Assert
        assertThat(booked.seats()).isEqualTo(2);
        verify(payments).pay("event 1, alice@example.com", new BigDecimal("78.00"));
    }

    @Test
    void savesNothingWhenThePaymentFails() {
        // Arrange
        when(events.findById(1L)).thenReturn(Optional.of(jazzNight));
        when(bookings.countBookedSeats(1L)).thenReturn(0L);
        when(payments.pay(any(), any()))
                .thenThrow(new PaymentFailedException(HttpStatus.PAYMENT_REQUIRED));

        // Act
        Throwable thrown = catchThrowable(
                () -> service.book(1L, new BookRequest("alice@example.com", 2)));

        // Assert
        assertThat(thrown).isInstanceOf(PaymentFailedException.class);
        verify(bookings, never()).save(any());
    }
}
