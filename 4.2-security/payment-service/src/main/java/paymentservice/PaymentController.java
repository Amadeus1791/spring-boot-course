package paymentservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

// The other team's service, a stand-in for a real payment provider. Not part of the course content.
@RestController
public class PaymentController {

    record PaymentRequest(String reference, BigDecimal amount) {
    }

    record Payment(String paymentId, String status, String reference, BigDecimal amount) {
    }

    private final Map<String, Payment> payments = new ConcurrentHashMap<>();
    private final long delayMillis;
    private final double failureRate;
    private final BigDecimal declineAbove;   // null: every payment is approved

    PaymentController(@Value("${payment.delay:0ms}") Duration delay,
                      @Value("${payment.failure-rate:0.0}") double failureRate,
                      @Value("${payment.decline-above:#{null}}") BigDecimal declineAbove) {
        this.delayMillis = delay.toMillis();
        this.failureRate = failureRate;
        this.declineAbove = declineAbove;
    }

    @PostMapping("/payments")
    ResponseEntity<Payment> pay(@RequestBody PaymentRequest request) throws InterruptedException {
        Thread.sleep(delayMillis);
        if (ThreadLocalRandom.current().nextDouble() < failureRate) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
        // Like a card limit: a declined payment is a normal answer (200), not an error.
        String status = "APPROVED";
        if (declineAbove != null && request.amount().compareTo(declineAbove) > 0) {
            status = "DECLINED";
        }
        Payment payment = new Payment(UUID.randomUUID().toString(), status,
                request.reference(), request.amount());
        payments.put(payment.paymentId(), payment);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/payments/{id}")
    ResponseEntity<Payment> find(@PathVariable String id) throws InterruptedException {
        Thread.sleep(delayMillis);
        if (ThreadLocalRandom.current().nextDouble() < failureRate) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
        Payment payment = payments.get(id);
        if (payment == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(payment);
    }
}
