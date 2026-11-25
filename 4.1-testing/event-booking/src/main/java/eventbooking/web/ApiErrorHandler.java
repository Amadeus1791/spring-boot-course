package eventbooking.web;

import eventbooking.booking.BookingNotFoundException;
import eventbooking.booking.SeatsNotAvailableException;
import eventbooking.event.EventNotFoundException;
import eventbooking.payment.PaymentDeclinedException;
import eventbooking.payment.PaymentFailedException;
import eventbooking.payment.PaymentTimeoutException;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.data.core.PropertyReferenceException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;
import java.util.TreeMap;

// Turns exceptions into one error format for the whole API (RFC 9457).
// The base class does the same for Spring's own errors: broken JSON, a wrong type in the URL, 415 and more.
@RestControllerAdvice
public class ApiErrorHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(EventNotFoundException.class)
    public ProblemDetail notFound(EventNotFoundException e) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND, e.getMessage());
        problem.setTitle("Event not found");
        return problem;
    }

    @ExceptionHandler(BookingNotFoundException.class)
    public ProblemDetail bookingNotFound(BookingNotFoundException e) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND, e.getMessage());
        problem.setTitle("Booking not found");
        return problem;
    }

    @ExceptionHandler(SeatsNotAvailableException.class)
    public ProblemDetail seatsNotAvailable(SeatsNotAvailableException e) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT, e.getMessage());
        problem.setTitle("Not enough seats");
        return problem;
    }

    @ExceptionHandler(PaymentFailedException.class)
    public ProblemDetail paymentFailed(PaymentFailedException e) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_GATEWAY, e.getMessage());
        problem.setTitle("Payment failed");
        return problem;
    }

    @ExceptionHandler(PaymentDeclinedException.class)
    public ProblemDetail paymentDeclined(PaymentDeclinedException e) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.PAYMENT_REQUIRED, e.getMessage());
        problem.setTitle("Payment declined");
        return problem;
    }

    @ExceptionHandler(PaymentTimeoutException.class)
    public ProblemDetail paymentTimeout(PaymentTimeoutException e) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.GATEWAY_TIMEOUT, e.getMessage());
        problem.setTitle("Payment not answered");
        return problem;
    }

    @ExceptionHandler(PropertyReferenceException.class)
    public ProblemDetail invalidSort(PropertyReferenceException e) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, e.getMessage());
        problem.setTitle("Invalid sort");
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

    // A constraint on a parameter, for example @Email on @RequestParam: named like a field.
    @Override
    protected ResponseEntity<Object> handleHandlerMethodValidationException(
            HandlerMethodValidationException e, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        Map<String, String> errors = new TreeMap<>();
        for (ParameterValidationResult result : e.getParameterValidationResults()) {
            String name = result.getMethodParameter().getParameterName();
            for (MessageSourceResolvable error : result.getResolvableErrors()) {
                errors.put(name, error.getDefaultMessage());
            }
        }
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                errors.size() + " parameter(s) are invalid");
        problem.setTitle("Invalid input");
        problem.setProperty("errors", errors);
        return ResponseEntity.badRequest().body(problem);
    }
}
