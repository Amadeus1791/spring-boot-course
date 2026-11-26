package eventbooking.payment;

import java.math.BigDecimal;

public record PaymentDetails(String paymentId, String status, BigDecimal amount) {
}
