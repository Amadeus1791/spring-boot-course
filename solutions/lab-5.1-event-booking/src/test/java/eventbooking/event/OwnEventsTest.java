package eventbooking.event;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

// EVB-20: organizers change only the prices of their own events.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
class OwnEventsTest {

    @Autowired
    RestTestClient client;

    @Test
    void onlyTheOrganizerOfAnEventChangesItsPrice() {
        // Arrange: olivia created the events of the Flyway migration
        String price = "{\"price\": 41.00}";

        // Act and Assert
        client.put().uri("/api/events/1/price")
                .headers(headers -> headers.setBasicAuth("oscar@example.com", "oscar"))
                .contentType(MediaType.APPLICATION_JSON).body(price)
                .exchange().expectStatus().isForbidden();
        client.put().uri("/api/events/1/price")
                .headers(headers -> headers.setBasicAuth("olivia@example.com", "olivia"))
                .contentType(MediaType.APPLICATION_JSON).body(price)
                .exchange().expectStatus().isOk();
    }
}
