package reviewservice.review;

import java.time.LocalDateTime;

public record ReviewResponse(Long id, Long eventId, String author, int stars, String text,
                             LocalDateTime createdAt) {

    static ReviewResponse from(Review review) {
        return new ReviewResponse(review.getId(), review.getEventId(), review.getAuthor(),
                review.getStars(), review.getText(), review.getCreatedAt());
    }
}
