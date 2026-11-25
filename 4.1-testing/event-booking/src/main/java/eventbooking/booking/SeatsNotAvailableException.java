package eventbooking.booking;

public class SeatsNotAvailableException extends RuntimeException {

    public SeatsNotAvailableException(Long eventId, long freeSeats) {
        super("Event " + eventId + " has only " + freeSeats + " free seat(s)");
    }
}
