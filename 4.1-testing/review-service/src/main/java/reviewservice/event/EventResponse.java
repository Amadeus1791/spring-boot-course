package reviewservice.event;

import java.time.LocalDateTime;

// Only the part of the event service's answer that the reviews need.
public record EventResponse(Long id, LocalDateTime startsAt) {
}
