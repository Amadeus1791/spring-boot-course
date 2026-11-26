package reviewservice.review;

import java.math.BigDecimal;

// average is null while an event has no reviews: no review is not the same as 0 stars.
public record RatingResponse(Long eventId, BigDecimal average, long count) {
}
