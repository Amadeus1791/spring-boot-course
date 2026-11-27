package eventbooking.event;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EventService {

    private final EventRepository repository;

    public EventService(EventRepository repository) {
        this.repository = repository;
    }

    public List<EventResponse> findAll() {
        List<EventResponse> result = new ArrayList<>();
        for (Event event : repository.findAllByOrderByStartsAt()) {
            result.add(EventResponse.from(event));
        }
        return result;
    }

    public EventResponse findById(Long id) {
        Event event = repository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(id));
        return EventResponse.from(event);
    }

    public EventResponse create(CreateEventRequest request, String organizer) {
        Event event = new Event(request.title(), request.city(), request.startsAt(),
                request.capacity(), request.price(), organizer);
        Event saved = repository.save(event);
        return EventResponse.from(saved);
    }

    @PreAuthorize("@eventAccess.isOrganizer(#id, authentication.name)")
    public EventResponse changePrice(Long id, ChangePriceRequest request) {
        Event event = repository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(id));
        event.changePrice(request.price());
        Event saved = repository.save(event);
        return EventResponse.from(saved);
    }
}
