package reviewservice.review;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import reviewservice.security.SecurityConfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

// (2) Web slice: controller, error handler, JSON. The service is a mock.
// Since 4.2 with our SecurityConfig: without it the slice gets Boot's default chain.
@WebMvcTest(ReviewController.class)
@Import(SecurityConfig.class)
class ReviewControllerTest {

    @Autowired
    MockMvcTester mvc;

    @MockitoBean
    ReviewService service;

    @Test
    @WithAnonymousUser
    void answers404AsAProblemWithItsOwnTitle() {
        // Arrange
        when(service.findById(999L)).thenThrow(new ReviewNotFoundException(999L));

        // Act
        MvcTestResult result = mvc.get().uri("/api/reviews/999").exchange();

        // Assert
        assertThat(result).hasStatus(HttpStatus.NOT_FOUND)
                .hasContentType(MediaType.APPLICATION_PROBLEM_JSON);
        assertThat(result).bodyJson().extractingPath("$.title").isEqualTo("Review not found");
    }

    @Test
    @WithMockUser(username = "alice@example.com", roles = "CUSTOMER")
    void refusesWrongInputPerFieldAndNeverCallsTheService() {
        // Act
        MvcTestResult result = mvc.post().uri("/api/events/1/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"stars\": 6, \"text\": \"%s\"}".formatted("x".repeat(501)))
                .exchange();

        // Assert
        assertThat(result).hasStatus(HttpStatus.BAD_REQUEST)
                .hasContentType(MediaType.APPLICATION_PROBLEM_JSON);
        assertThat(result).bodyJson().extractingPath("$.errors.text").isNotNull();
        assertThat(result).bodyJson().extractingPath("$.errors.stars").isNotNull();
        verifyNoInteractions(service);
    }

    @Test
    @WithMockUser(username = "olivia@example.com", roles = "ORGANIZER")
    void anOrganizerMayNotWriteAReview() {
        // Act
        MvcTestResult result = mvc.post().uri("/api/events/1/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"stars\": 5}")
                .exchange();

        // Assert
        assertThat(result).hasStatus(HttpStatus.FORBIDDEN);
        verifyNoInteractions(service);
    }
}
