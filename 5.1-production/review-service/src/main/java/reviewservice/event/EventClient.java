package reviewservice.event;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

@Component
public class EventClient {

    private final RestClient restClient;
    private final Duration timeout;

    public EventClient(RestClient.Builder builder, EventsProperties properties) {
        this.timeout = properties.timeout();
        HttpClient httpClient = HttpClient.newBuilder().connectTimeout(timeout).build();
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(timeout);
        this.restClient = builder
                .baseUrl(properties.baseUrl().toString())
                .requestFactory(requestFactory)
                .build();
    }

    public EventResponse find(Long id) {
        try {
            return restClient.get()
                    .uri("/api/events/{id}", id)
                    .retrieve()
                    // The first matching handler wins: 404 first, then every other error.
                    .onStatus(status -> status.value() == 404, (request, response) -> {
                        throw new EventNotFoundException(id);
                    })
                    .onStatus(HttpStatusCode::isError, (request, response) -> {
                        throw new EventServiceFailedException(response.getStatusCode());
                    })
                    .body(EventResponse.class);
        } catch (ResourceAccessException e) {
            throw new EventServiceTimeoutException(timeout, e);
        }
    }
}
