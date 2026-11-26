package reviewservice.review;

import java.time.LocalDateTime;

public class EventNotOverException extends RuntimeException {

    public EventNotOverException(Long eventId, LocalDateTime startsAt) {
        super("Event " + eventId + " starts at " + startsAt + ", reviews open after that");
    }
}
