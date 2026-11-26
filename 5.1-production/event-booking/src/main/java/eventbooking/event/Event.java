package eventbooking.event;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String city;
    private LocalDateTime startsAt;
    private int capacity;
    private BigDecimal price;
    private String organizer;

    protected Event() {
    }

    public Event(String title, String city, LocalDateTime startsAt, int capacity, BigDecimal price,
                 String organizer) {
        this.title = title;
        this.city = city;
        this.startsAt = startsAt;
        this.capacity = capacity;
        this.price = price;
        this.organizer = organizer;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCity() {
        return city;
    }

    public LocalDateTime getStartsAt() {
        return startsAt;
    }

    public int getCapacity() {
        return capacity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getOrganizer() {
        return organizer;
    }

    public void changePrice(BigDecimal newPrice) {
        this.price = newPrice;
    }
}
