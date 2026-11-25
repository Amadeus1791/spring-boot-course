package reviewservice.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

// PUT replaces what the author may change: stars and text. Author, event and time stay.
public record ChangeReviewRequest(
        @Min(1) @Max(5) int stars,
        @Size(max = 500) String text) {
}
