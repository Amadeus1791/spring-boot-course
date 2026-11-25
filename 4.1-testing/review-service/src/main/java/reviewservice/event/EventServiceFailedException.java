package reviewservice.event;

import org.springframework.http.HttpStatusCode;

public class EventServiceFailedException extends RuntimeException {

    public EventServiceFailedException(HttpStatusCode status) {
        super("The event service answered " + status.value());
    }
}
