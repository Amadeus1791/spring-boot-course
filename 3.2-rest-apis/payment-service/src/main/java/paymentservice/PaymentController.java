package paymentservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

// The other team's service, a stand-in for a real payment provider. Not part of the course content.
@RestController
public class PaymentController {

    record PaymentRequest(String reference, BigDecimal amount) {
    }

    record PaymentResponse(String paymentId, String status) {
    }

    private final long delayMillis;
    private final double failureRate;

    PaymentController(@Value("${payment.delay:0ms}") java.time.Duration delay,
                      @Value("${payment.failure-rate:0.0}") double failureRate) {
        this.delayMillis = delay.toMillis();
        this.failureRate = failureRate;
    }

    @PostMapping("/payments")
    ResponseEntity<PaymentResponse> pay(@RequestBody PaymentRequest request) throws InterruptedException {
        Thread.sleep(delayMillis);
        if (ThreadLocalRandom.current().nextDouble() < failureRate) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
        return ResponseEntity.ok(new PaymentResponse(UUID.randomUUID().toString(), "APPROVED"));
    }
}
