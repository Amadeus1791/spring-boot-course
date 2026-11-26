package reviewservice.review;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.security.Principal;

@RestController
public class ReviewController {

    private final ReviewService service;

    public ReviewController(ReviewService service) {
        this.service = service;
    }

    @PostMapping("/api/events/{eventId}/reviews")
    public ResponseEntity<ReviewResponse> create(@PathVariable Long eventId,
                                                 @Valid @RequestBody CreateReviewRequest request,
                                                 Principal author) {
        ReviewResponse created = service.create(eventId, request, author.getName());
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/reviews/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/api/reviews/{id}")
    public ReviewResponse byId(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/api/reviews/{id}")
    public ReviewResponse change(@PathVariable Long id, @Valid @RequestBody ChangeReviewRequest request,
                                 Principal user) {
        return service.change(id, request, user.getName());
    }

    @GetMapping("/api/events/{eventId}/reviews")
    public Page<ReviewResponse> byEvent(
            @PathVariable Long eventId,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return service.findByEvent(eventId, pageable);
    }

    @GetMapping("/api/events/{eventId}/rating")
    public RatingResponse rating(@PathVariable Long eventId) {
        return service.rating(eventId);
    }
}
