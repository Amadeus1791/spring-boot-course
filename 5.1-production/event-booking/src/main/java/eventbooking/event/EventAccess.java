package eventbooking.event;

import org.springframework.stereotype.Component;

// Used in @PreAuthorize: did this user create this event?
@Component
public class EventAccess {

    private final EventRepository events;

    public EventAccess(EventRepository events) {
        this.events = events;
    }

    public boolean isOrganizer(Long eventId, String username) {
        return events.findById(eventId)
                .map(event -> username.equals(event.getOrganizer()))
                .orElse(true);   // unknown event: let the service answer 404
    }
}
