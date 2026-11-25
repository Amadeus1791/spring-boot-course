package reviewservice.review;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Only the number: the event lives in another service, so no relation.
    private Long eventId;
    private String author;
    private int stars;
    private String text;
    private LocalDateTime createdAt;

    protected Review() {
    }

    public Review(Long eventId, String author, int stars, String text, LocalDateTime createdAt) {
        this.eventId = eventId;
        this.author = author;
        this.stars = stars;
        this.text = text;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getEventId() {
        return eventId;
    }

    public String getAuthor() {
        return author;
    }

    public int getStars() {
        return stars;
    }

    public String getText() {
        return text;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void change(int stars, String text) {
        this.stars = stars;
        this.text = text;
    }
}
