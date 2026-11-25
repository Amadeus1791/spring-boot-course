package reviewservice.review;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
public class ReviewController {

    private final ReviewService service;

    public ReviewController(ReviewService service) {
        this.service = service;
    }

    @PostMapping("/api/events/{eventId}/reviews")
    public ResponseEntity<ReviewResponse> create(@PathVariable Long eventId,
                                                 @Valid @RequestBody CreateReviewRequest request) {
        ReviewResponse created = service.create(eventId, request);
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/reviews/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/api/reviews/{id}")
    public ReviewResponse byId(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/api/reviews/{id}")
    public ReviewResponse change(@PathVariable Long id, @Valid @RequestBody ChangeReviewRequest request) {
        return service.change(id, request);
    }
}
