package eventbooking.booking;

public class BookingNotFoundException extends RuntimeException {

    public BookingNotFoundException(Long id) {
        super("Booking " + id + " does not exist");
    }
}
