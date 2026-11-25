package reviewservice.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reviewservice.review.ReviewNotFoundException;
import reviewservice.review.TextRequiredException;

import java.util.Map;
import java.util.TreeMap;

// Turns exceptions into one error format for the whole API (RFC 9457).
@RestControllerAdvice
public class ApiErrorHandler {

    @ExceptionHandler(ReviewNotFoundException.class)
    public ProblemDetail reviewNotFound(ReviewNotFoundException e) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND, e.getMessage());
        problem.setTitle("Review not found");
        return problem;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail invalidInput(MethodArgumentNotValidException e) {
        Map<String, String> errors = new TreeMap<>();
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                errors.size() + " field(s) are invalid");
        problem.setTitle("Invalid input");
        problem.setProperty("errors", errors);
        return problem;
    }

    // Same shape as the field errors: the client finds the problem under errors.text.
    @ExceptionHandler(TextRequiredException.class)
    public ProblemDetail textRequired(TextRequiredException e) {
        Map<String, String> errors = new TreeMap<>();
        errors.put("text", e.getMessage());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "1 field(s) are invalid");
        problem.setTitle("Invalid input");
        problem.setProperty("errors", errors);
        return problem;
    }
}
