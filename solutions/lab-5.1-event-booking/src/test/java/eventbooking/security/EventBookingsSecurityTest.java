package eventbooking.security;

import eventbooking.booking.BookingController;
import eventbooking.booking.BookingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import static org.assertj.core.api.Assertions.assertThat;

// EVB-17: the list of an event is for organizers (URL rule, web layer only)
@WebMvcTest(BookingController.class)
@Import(SecurityConfig.class)
class EventBookingsSecurityTest {

    @Autowired
    MockMvcTester mvc;

    @MockitoBean
    BookingService service;

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void aCustomerGets403() {
        // Act and Assert
        assertThat(mvc.get().uri("/api/events/1/bookings")).hasStatus(HttpStatus.FORBIDDEN);
    }

    @Test
    @WithAnonymousUser
    void nobodyGets401() {
        // Act and Assert
        assertThat(mvc.get().uri("/api/events/1/bookings")).hasStatus(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @WithMockUser(roles = "ORGANIZER")
    void anOrganizerGets200() {
        // Act and Assert
        assertThat(mvc.get().uri("/api/events/1/bookings")).hasStatus(HttpStatus.OK);
    }
}
