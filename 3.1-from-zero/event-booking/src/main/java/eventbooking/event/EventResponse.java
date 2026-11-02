package eventbooking.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Chapter 3.2: the answer as a record, so the entity never leaves the service.
public record EventResponse(Long id, String title, String city,
                            LocalDateTime startsAt, int capacity, BigDecimal price) {

    static EventResponse from(Event event) {
        return new EventResponse(event.getId(), event.getTitle(), event.getCity(),
                event.getStartsAt(), event.getCapacity(), event.getPrice());
    }
}
