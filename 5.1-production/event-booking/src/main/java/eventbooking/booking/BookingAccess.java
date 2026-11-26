package eventbooking.booking;

import org.springframework.stereotype.Component;

// Used in @PreAuthorize: is this user the customer of this booking?
@Component
public class BookingAccess {

    private final BookingRepository bookings;

    public BookingAccess(BookingRepository bookings) {
        this.bookings = bookings;
    }

    public boolean isOwner(Long bookingId, String username) {
        return bookings.findById(bookingId)
                .map(booking -> booking.getCustomerEmail().equals(username))
                .orElse(true);   // unknown booking: let the service answer 404
    }
}
