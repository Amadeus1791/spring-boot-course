package reviewservice.review;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ReviewService {

    private final ReviewRepository reviews;

    public ReviewService(ReviewRepository reviews) {
        this.reviews = reviews;
    }

    public ReviewResponse create(Long eventId, CreateReviewRequest request) {
        checkText(request.stars(), request.text());
        Review review = new Review(eventId, request.author(), request.stars(), request.text(),
                LocalDateTime.now());
        Review saved = reviews.save(review);
        return ReviewResponse.from(saved);
    }

    public ReviewResponse findById(Long id) {
        Review review = reviews.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException(id));
        return ReviewResponse.from(review);
    }

    public ReviewResponse change(Long id, ChangeReviewRequest request) {
        Review review = reviews.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException(id));
        // The same rule as when creating: 1 or 2 stars need a text.
        checkText(request.stars(), request.text());
        review.change(request.stars(), request.text());
        Review saved = reviews.save(review);
        return ReviewResponse.from(saved);
    }

    // One rule for every way that writes a review.
    private void checkText(int stars, String text) {
        if (stars <= 2 && (text == null || text.isBlank())) {
            throw new TextRequiredException(stars);
        }
    }
}
