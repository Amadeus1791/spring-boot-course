package eventbooking.payment;

public class PaymentDeclinedException extends RuntimeException {

    public PaymentDeclinedException(String paymentId) {
        super("The payment " + paymentId + " was declined");
    }
}
