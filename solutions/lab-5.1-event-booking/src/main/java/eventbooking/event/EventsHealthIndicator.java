package eventbooking.event;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

// Shown as "events" in /actuator/health: is there anything to book?
@Component
public class EventsHealthIndicator implements HealthIndicator {

    private final EventRepository events;

    public EventsHealthIndicator(EventRepository events) {
        this.events = events;
    }

    @Override
    public Health health() {
        long count = events.count();
        if (count == 0) {
            return Health.down().withDetail("reason", "no events to book").build();
        }
        return Health.up().withDetail("events", count).build();
    }
}
