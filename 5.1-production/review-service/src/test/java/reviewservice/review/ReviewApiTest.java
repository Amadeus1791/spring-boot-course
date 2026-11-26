package reviewservice.review;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient;
import reviewservice.event.EventClient;
import reviewservice.event.EventResponse;

import java.net.URI;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

// (4) Integration: the whole service on a real port, only the event service is a mock.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
class ReviewApiTest {

    @Autowired
    RestTestClient client;

    @MockitoBean
    EventClient events;

    @Test
    void createsAReviewAndReadsItBack() {
        // Arrange
        when(events.find(1L)).thenReturn(new EventResponse(1L, LocalDateTime.now().minusDays(1)));

        // Act
        URI location = client.post().uri("/api/events/1/reviews")
                .headers(headers -> headers.setBasicAuth("alice@example.com", "alice"))
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"stars\": 4, \"text\": \"Great evening.\"}")
                .exchange()
                .expectStatus().isCreated()
                .returnResult().getResponseHeaders().getLocation();

        // Assert
        assertThat(location).isNotNull();
        client.get().uri(location)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.author").isEqualTo("alice@example.com");
    }

    @Test
    void refusesAnEventThatHasNotTakenPlace() {
        // Arrange
        when(events.find(2L)).thenReturn(new EventResponse(2L, LocalDateTime.now().plusDays(30)));

        // Act and Assert
        client.post().uri("/api/events/2/reviews")
                .headers(headers -> headers.setBasicAuth("alice@example.com", "alice"))
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"stars\": 4, \"text\": \"Looking forward.\"}")
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON);
    }

    @Test
    void onlyTheAuthorChangesAReview() {
        // Arrange
        when(events.find(1L)).thenReturn(new EventResponse(1L, LocalDateTime.now().minusDays(1)));
        URI location = client.post().uri("/api/events/1/reviews")
                .headers(headers -> headers.setBasicAuth("alice@example.com", "alice"))
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"stars\": 4, \"text\": \"Great evening.\"}")
                .exchange()
                .expectStatus().isCreated()
                .returnResult().getResponseHeaders().getLocation();

        // Act
        client.put().uri(location)
                .headers(headers -> headers.setBasicAuth("ben@example.com", "ben"))
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"stars\": 1, \"text\": \"Terrible.\"}")
                .exchange()
                .expectStatus().isForbidden();

        // Assert
        client.get().uri(location)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.stars").isEqualTo(4);
    }

    @Test
    void writingNeedsALogin() {
        // Act and Assert
        client.post().uri("/api/events/1/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"stars\": 4, \"text\": \"Great evening.\"}")
                .exchange()
                .expectStatus().isUnauthorized();
    }
}
