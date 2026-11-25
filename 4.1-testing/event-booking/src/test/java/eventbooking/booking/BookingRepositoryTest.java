package eventbooking.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;

// Only JPA: H2, Flyway, repositories. Each test runs in a transaction that is rolled back.
@DataJpaTest
@Sql("/bookings.sql")
class BookingRepositoryTest {

    @Autowired
    BookingRepository bookings;

    @Test
    void sumsTheBookedSeatsOfOneEvent() {
        // Act
        long booked = bookings.countBookedSeats(1L);

        // Assert
        assertThat(booked).isEqualTo(5);
    }

    @Test
    void findsTheBookingsOfACustomerNewestFirst() {
        // Arrange
        PageRequest firstPage = PageRequest.of(0, 10, Sort.by("createdAt").descending());

        // Act
        Page<Booking> page = bookings.findByCustomerEmail("alice@example.com", firstPage);

        // Assert
        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getContent().get(0).getPaymentId()).isEqualTo("p-3");
    }

    @Test
    void deletingEverythingStaysInThisTest() {
        // Act
        bookings.deleteAll();

        // Assert
        assertThat(bookings.count()).isZero();
    }

    @Test
    void everyTestStartsWithTheSameThreeBookings() {
        // Act
        long count = bookings.count();

        // Assert
        assertThat(count).isEqualTo(3);
    }

    @Test
    void findsAlicesBookingOnPageOne() {
        // Arrange
        PageRequest secondPage = PageRequest.of(1, 1, Sort.by("createdAt").descending());

        // Act
        Page<Booking> page = bookings.findByCustomerEmail("alice@example.com", secondPage);

        // Assert
        assertThat(page.getContent().get(0).getPaymentId()).isEqualTo("p-1");
    }
}
