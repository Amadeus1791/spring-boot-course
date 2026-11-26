package reviewservice.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

// Since chapter 4.2 the author is who is logged in: the body names only stars and text.
public record CreateReviewRequest(
        @Min(1) @Max(5) int stars,
        @Size(max = 500) String text) {
}
