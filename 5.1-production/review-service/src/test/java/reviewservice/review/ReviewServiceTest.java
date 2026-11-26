package reviewservice.review;

import org.junit.jupiter.api.Test;
import reviewservice.event.EventClient;
import reviewservice.event.EventResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

// (1) Unit: the rules of the service without Spring. Repository and event service are mocks.
class ReviewServiceTest {

    private final ReviewRepository reviews = mock(ReviewRepository.class);
    private final EventClient events = mock(EventClient.class);
    private final ReviewService service = new ReviewService(reviews, events);

    private final EventResponse yesterday = new EventResponse(1L, LocalDateTime.now().minusDays(1));

    @Test
    void createRefusesOneStarWithoutTextAndAsksNobody() {
        // Arrange
        CreateReviewRequest request = new CreateReviewRequest(1, null);

        // Act
        Throwable thrown = catchThrowable(() -> service.create(1L, request, "ben@example.com"));

        // Assert
        assertThat(thrown).isInstanceOf(TextRequiredException.class)
                .hasMessage("A review with 1 star(s) needs a text");
        verifyNoInteractions(events);
        verify(reviews, never()).save(any());
    }

    @Test
    void changeRefusesTwoStarsWithoutText() {
        // Arrange
        Review stored = new Review(1L, "alice@example.com", 4, "Good.", LocalDateTime.now());
        when(reviews.findById(7L)).thenReturn(Optional.of(stored));

        // Act
        Throwable thrown = catchThrowable(
                () -> service.change(7L, new ChangeReviewRequest(2, " "), "alice@example.com"));

        // Assert
        assertThat(thrown).isInstanceOf(TextRequiredException.class);
        verify(reviews, never()).save(any());
    }

    @Test
    void createAcceptsFiveStarsWithoutText() {
        // Arrange
        when(events.find(1L)).thenReturn(yesterday);
        when(reviews.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ReviewResponse created = service.create(1L, new CreateReviewRequest(5, null), "cleo@example.com");

        // Assert
        assertThat(created.stars()).isEqualTo(5);
        assertThat(created.text()).isNull();
    }

    @Test
    void createRefusesAnEventThatHasNotTakenPlace() {
        // Arrange
        when(events.find(2L)).thenReturn(new EventResponse(2L, LocalDateTime.now().plusDays(30)));

        // Act
        Throwable thrown = catchThrowable(
                () -> service.create(2L, new CreateReviewRequest(4, "Looking forward."), "alice@example.com"));

        // Assert
        assertThat(thrown).isInstanceOf(EventNotOverException.class);
        verify(reviews, never()).save(any());
    }

    @Test
    void changeRefusesAReviewOfSomeoneElse() {
        // Arrange
        Review stored = new Review(1L, "alice@example.com", 4, "Good.", LocalDateTime.now());
        when(reviews.findById(7L)).thenReturn(Optional.of(stored));

        // Act
        Throwable thrown = catchThrowable(
                () -> service.change(7L, new ChangeReviewRequest(1, "Bad."), "ben@example.com"));

        // Assert
        assertThat(thrown).isInstanceOf(NotTheAuthorException.class);
        assertThat(stored.getStars()).isEqualTo(4);
        verify(reviews, never()).save(any());
    }

    @Test
    void createTakesTheAuthorFromTheLogin() {
        // Arrange
        when(events.find(1L)).thenReturn(yesterday);
        when(reviews.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ReviewResponse created = service.create(1L, new CreateReviewRequest(4, "Fine."), "alice@example.com");

        // Assert
        assertThat(created.author()).isEqualTo("alice@example.com");
    }

    @Test
    void ratingWithoutReviewsHasNoAverage() {
        // Arrange
        when(reviews.countByEventId(3L)).thenReturn(0L);
        when(reviews.averageStars(3L)).thenReturn(null);

        // Act
        RatingResponse rating = service.rating(3L);

        // Assert
        assertThat(rating.average()).isNull();
        assertThat(rating.count()).isZero();
    }

    @Test
    void ratingRoundsToOneDecimal() {
        // Arrange
        when(reviews.countByEventId(1L)).thenReturn(3L);
        when(reviews.averageStars(1L)).thenReturn(11.0 / 3);

        // Act
        RatingResponse rating = service.rating(1L);

        // Assert
        assertThat(rating.average()).isEqualTo(new BigDecimal("3.7"));
    }
}
