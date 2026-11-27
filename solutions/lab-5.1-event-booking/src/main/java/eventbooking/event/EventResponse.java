package eventbooking.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EventResponse(Long id, String title, String city,
                            LocalDateTime startsAt, int capacity, BigDecimal price,
                            String organizer) {

    static EventResponse from(Event event) {
        return new EventResponse(event.getId(), event.getTitle(), event.getCity(),
                event.getStartsAt(), event.getCapacity(), event.getPrice(), event.getOrganizer());
    }
}
