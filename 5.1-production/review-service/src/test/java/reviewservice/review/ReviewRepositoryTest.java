package reviewservice.review;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

// (3) Data slice: H2, Flyway, the repository. Each test is rolled back.
@DataJpaTest
@Sql("/reviews.sql")
class ReviewRepositoryTest {

    @Autowired
    ReviewRepository reviews;

    @Test
    void averageOverNoReviewsIsNull() {
        // Act
        Double average = reviews.averageStars(2L);

        // Assert
        assertThat(average).isNull();
    }

    @Test
    void averageComesFromTheDatabase() {
        // Act
        Double average = reviews.averageStars(1L);

        // Assert
        assertThat(average).isCloseTo(11.0 / 3, within(0.001));
    }

    @Test
    void reviewsOfAnEventNewestFirst() {
        // Arrange
        PageRequest firstPage = PageRequest.of(0, 2, Sort.by("createdAt").descending());

        // Act
        Page<Review> page = reviews.findByEventId(1L, firstPage);

        // Assert
        assertThat(page.getTotalElements()).isEqualTo(3);
        assertThat(page.getContent().get(0).getAuthor()).isEqualTo("Cleo");
    }
}
