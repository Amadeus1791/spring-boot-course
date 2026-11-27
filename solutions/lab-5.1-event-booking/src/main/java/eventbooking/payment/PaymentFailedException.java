package eventbooking.payment;

import org.springframework.http.HttpStatusCode;

public class PaymentFailedException extends RuntimeException {

    public PaymentFailedException(HttpStatusCode status) {
        super("The payment service answered " + status.value());
    }
}
