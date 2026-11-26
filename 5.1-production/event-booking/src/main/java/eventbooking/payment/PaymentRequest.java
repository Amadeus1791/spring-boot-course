package eventbooking.payment;

import java.math.BigDecimal;

public record PaymentRequest(String reference, BigDecimal amount) {
}
