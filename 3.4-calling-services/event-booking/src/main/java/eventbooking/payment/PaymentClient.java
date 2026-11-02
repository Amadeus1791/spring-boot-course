package eventbooking.payment;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.net.http.HttpClient;
import java.time.Duration;

@Component
public class PaymentClient {

    private final RestClient restClient;
    private final Duration timeout;

    public PaymentClient(RestClient.Builder builder, PaymentProperties properties) {
        this.timeout = properties.timeout();
        HttpClient httpClient = HttpClient.newBuilder().connectTimeout(timeout).build();
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(timeout);
        this.restClient = builder
                .baseUrl(properties.baseUrl().toString())
                .requestFactory(requestFactory)
                .build();
    }

    public PaymentResponse pay(String reference, BigDecimal amount) {
        try {
            return restClient.post()
                    .uri("/payments")
                    .body(new PaymentRequest(reference, amount))
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, response) -> {
                        throw new PaymentFailedException(response.getStatusCode());
                    })
                    .body(PaymentResponse.class);
        } catch (ResourceAccessException e) {
            throw new PaymentTimeoutException(timeout, e);
        }
    }
}
