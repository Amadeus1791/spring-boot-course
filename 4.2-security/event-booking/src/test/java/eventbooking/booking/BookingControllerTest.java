package eventbooking.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import eventbooking.security.SecurityConfig;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

// Only the web layer: controller, error handler, JSON. The service is a mock.
@WebMvcTest(BookingController.class)
@Import(SecurityConfig.class)
@WithMockUser(username = "alice@example.com", roles = "CUSTOMER")
class BookingControllerTest {

    @Autowired
    MockMvcTester mvc;

    @MockitoBean
    BookingService service;

    @Test
    void answers409AsAProblemWhenTheSeatsAreTaken() {
        // Arrange
        when(service.book(eq(1L), any(), any())).thenThrow(new SeatsNotAvailableException(1L, 2));

        // Act
        MvcTestResult result = mvc.post().uri("/api/events/1/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"seats\": 3}")
                .exchange();

        // Assert
        assertThat(result).hasStatus(HttpStatus.CONFLICT)
                .hasContentType(MediaType.APPLICATION_PROBLEM_JSON);
        assertThat(result).bodyJson().extractingPath("$.title").isEqualTo("Not enough seats");
        assertThat(result).bodyJson().extractingPath("$.detail")
                .isEqualTo("Event 1 has only 2 free seat(s)");
    }

    @Test
    void answers400AsAProblemForTooManySeats() {
        // Arrange
        when(service.book(eq(1L), any(), any())).thenThrow(new TooManySeatsException(6));

        // Act
        MvcTestResult result = mvc.post().uri("/api/events/1/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"seats\": 7}")
                .exchange();

        // Assert
        assertThat(result).hasStatus(HttpStatus.BAD_REQUEST)
                .hasContentType(MediaType.APPLICATION_PROBLEM_JSON);
        assertThat(result).bodyJson().extractingPath("$.detail").isEqualTo("At most 6 seats per booking");
    }
}
