package reviewservice.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateReviewRequest(
        @NotBlank @Size(max = 100) String author,
        @Min(1) @Max(5) int stars,
        @Size(max = 500) String text) {
}
