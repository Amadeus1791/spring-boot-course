package eventbooking.booking;

public class TooManySeatsException extends RuntimeException {

    public TooManySeatsException(int maxSeats) {
        super("At most " + maxSeats + " seats per booking");
    }
}
