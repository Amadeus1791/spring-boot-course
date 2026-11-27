package eventbooking.payment;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.net.http.HttpClient;
import java.time.Duration;

// Shown as "payment" in /actuator/health: is the payment service there?
@Component
public class PaymentHealthIndicator implements HealthIndicator {

    private static final Duration TIMEOUT = Duration.ofSeconds(1);

    private final RestClient restClient;

    public PaymentHealthIndicator(RestClient.Builder builder, PaymentProperties properties) {
        HttpClient httpClient = HttpClient.newBuilder().connectTimeout(TIMEOUT).build();
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(TIMEOUT);
        this.restClient = builder
                .baseUrl(properties.baseUrl().toString())
                .requestFactory(requestFactory)
                .build();
    }

    @Override
    public Health health() {
        try {
            restClient.get().uri("/actuator/health").retrieve().toBodilessEntity();
            return Health.up().build();
        } catch (RestClientException e) {
            return Health.down().withDetail("error", e.getMessage()).build();
        }
    }
}
