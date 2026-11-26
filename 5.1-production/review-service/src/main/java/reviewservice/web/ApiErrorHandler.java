package reviewservice.web;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import reviewservice.event.EventNotFoundException;
import reviewservice.event.EventServiceFailedException;
import reviewservice.event.EventServiceTimeoutException;
import reviewservice.review.EventNotOverException;
import reviewservice.review.NotTheAuthorException;
import reviewservice.review.ReviewNotFoundException;
import reviewservice.review.TextRequiredException;

import java.util.Map;
import java.util.TreeMap;

// Turns exceptions into one error format for the whole API (RFC 9457).
// The base class does the same for Spring's own errors: broken JSON, a wrong type in the URL, 415 and more.
@RestControllerAdvice
public class ApiErrorHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ReviewNotFoundException.class)
    public ProblemDetail reviewNotFound(ReviewNotFoundException e) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND, e.getMessage());
        problem.setTitle("Review not found");
        return problem;
    }

    // 403, not 404: the review exists and anyone may read it, so hiding it would hide nothing.
    @ExceptionHandler(NotTheAuthorException.class)
    public ProblemDetail notTheAuthor(NotTheAuthorException e) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN, e.getMessage());
        problem.setTitle("Not your review");
        return problem;
    }

    @ExceptionHandler(EventNotFoundException.class)
    public ProblemDetail eventNotFound(EventNotFoundException e) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND, e.getMessage());
        problem.setTitle("Event not found");
        return problem;
    }

    @ExceptionHandler(EventNotOverException.class)
    public ProblemDetail eventNotOver(EventNotOverException e) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT, e.getMessage());
        problem.setTitle("Event has not taken place yet");
        return problem;
    }

    @ExceptionHandler(EventServiceFailedException.class)
    public ProblemDetail eventServiceFailed(EventServiceFailedException e) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_GATEWAY, e.getMessage());
        problem.setTitle("Event service failed");
        return problem;
    }

    @ExceptionHandler(EventServiceTimeoutException.class)
    public ProblemDetail eventServiceTimeout(EventServiceTimeoutException e) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.GATEWAY_TIMEOUT, e.getMessage());
        problem.setTitle("Event service not answered");
        return problem;
    }

    // The base class already answers invalid fields, without naming them. We override its method.
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e,
            HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, String> errors = new TreeMap<>();
        for (FieldError error : e.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                errors.size() + " field(s) are invalid");
        problem.setTitle("Invalid input");
        problem.setProperty("errors", errors);
        return ResponseEntity.badRequest().body(problem);
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
