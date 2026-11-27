package eventbooking.payment;

import java.time.Duration;

public class PaymentTimeoutException extends RuntimeException {

    public PaymentTimeoutException(Duration timeout, Throwable cause) {
        super("The payment service did not answer within " + timeout, cause);
    }
}
