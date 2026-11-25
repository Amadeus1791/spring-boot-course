package eventbooking.booking;

import eventbooking.payment.PaymentClient;
import eventbooking.payment.PaymentFailedException;
import eventbooking.payment.PaymentResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

// The whole service on a real port, only the payment service is a mock.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
class BookingApiTest {

    @Autowired
    RestTestClient client;

    @MockitoBean
    PaymentClient payments;

    @Autowired
    BookingRepository bookings;

    @Test
    void booksAndListsTheBooking() {
        // Arrange
        when(payments.pay(any(), any())).thenReturn(new PaymentResponse("p-42", "APPROVED"));

        // Act
        client.post().uri("/api/events/1/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"customerEmail\": \"ben@example.com\", \"seats\": 2}")
                .exchange()
                .expectStatus().isCreated();

        // Assert
        client.get().uri("/api/bookings?email=ben@example.com")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content[0].paymentId").isEqualTo("p-42");
    }

    @Test
    void storesNothingWhenThePaymentFails() {
        // Arrange
        when(payments.pay(any(), any())).thenThrow(new PaymentFailedException(HttpStatus.SERVICE_UNAVAILABLE));
        long before = bookings.count();

        // Act
        client.post().uri("/api/events/1/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"customerEmail\": \"alice@example.com\", \"seats\": 2}")
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_GATEWAY)
                .expectBody()
                .jsonPath("$.title").isEqualTo("Payment failed");

        // Assert
        assertThat(bookings.count()).isEqualTo(before);
    }

    @Test
    void answers400ForAnUnknownSortField() {
        // Act and Assert
        client.get().uri("/api/bookings?email=ben@example.com&sort=foo")
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .jsonPath("$.title").isEqualTo("Invalid sort");
    }
}
