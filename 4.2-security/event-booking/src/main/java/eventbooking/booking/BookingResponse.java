package eventbooking.booking;

import java.time.LocalDateTime;

public record BookingResponse(Long id, Long eventId, String eventTitle,
                              String customerEmail, int seats, LocalDateTime createdAt,
                              String paymentId) {

    static BookingResponse from(Booking booking) {
        return new BookingResponse(booking.getId(), booking.getEvent().getId(),
                booking.getEvent().getTitle(), booking.getCustomerEmail(),
                booking.getSeats(), booking.getCreatedAt(), booking.getPaymentId());
    }
}
