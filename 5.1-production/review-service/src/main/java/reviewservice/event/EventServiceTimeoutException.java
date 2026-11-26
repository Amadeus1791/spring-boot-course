package reviewservice.event;

import java.time.Duration;

public class EventServiceTimeoutException extends RuntimeException {

    public EventServiceTimeoutException(Duration timeout, Throwable cause) {
        super("The event service did not answer within " + timeout, cause);
    }
}
