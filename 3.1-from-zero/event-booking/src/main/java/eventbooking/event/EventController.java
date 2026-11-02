package eventbooking.event;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

// Chapter 3.2 builds endpoints like this one: @RestController, @GetMapping, an answer record.
@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventRepository repository;

    public EventController(EventRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<EventResponse> all() {
        List<EventResponse> result = new ArrayList<>();
        for (Event event : repository.findAllByOrderByStartsAt()) {
            result.add(EventResponse.from(event));
        }
        return result;
    }
}
