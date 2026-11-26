package reviewservice.review;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reviewservice.event.EventClient;
import reviewservice.event.EventResponse;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
public class ReviewService {

    private final ReviewRepository reviews;
    private final EventClient events;

    public ReviewService(ReviewRepository reviews, EventClient events) {
        this.reviews = reviews;
        this.events = events;
    }

    // No @Transactional: the remote call must not hold a database transaction open.
    public ReviewResponse create(Long eventId, CreateReviewRequest request, String author) {
        checkText(request.stars(), request.text());
        EventResponse event = events.find(eventId);
        if (event.startsAt().isAfter(LocalDateTime.now())) {
            throw new EventNotOverException(eventId, event.startsAt());
        }
        Review review = new Review(eventId, author, request.stars(), request.text(),
                LocalDateTime.now());
        Review saved = reviews.save(review);
        return ReviewResponse.from(saved);
    }

    public ReviewResponse findById(Long id) {
        Review review = reviews.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException(id));
        return ReviewResponse.from(review);
    }

    public ReviewResponse change(Long id, ChangeReviewRequest request, String user) {
        Review review = reviews.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException(id));
        // Only the author: decided on the loaded review, before anything changes.
        if (!review.getAuthor().equals(user)) {
            throw new NotTheAuthorException(id);
        }
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

    public Page<ReviewResponse> findByEvent(Long eventId, Pageable pageable) {
        return reviews.findByEventId(eventId, pageable).map(ReviewResponse::from);
    }

    public RatingResponse rating(Long eventId) {
        long count = reviews.countByEventId(eventId);
        Double average = reviews.averageStars(eventId);
        BigDecimal rounded = null;
        if (average != null) {
            rounded = BigDecimal.valueOf(average).setScale(1, RoundingMode.HALF_UP);
        }
        return new RatingResponse(eventId, rounded, count);
    }
}
