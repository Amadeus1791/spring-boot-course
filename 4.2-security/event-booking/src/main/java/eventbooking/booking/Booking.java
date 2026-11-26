package eventbooking.booking;

import eventbooking.event.Event;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import java.time.LocalDateTime;

@Entity
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Event event;

    private String customerEmail;
    private int seats;
    private LocalDateTime createdAt;
    private String paymentId;

    protected Booking() {
    }

    public Booking(Event event, String customerEmail, int seats, LocalDateTime createdAt,
                   String paymentId) {
        this.event = event;
        this.customerEmail = customerEmail;
        this.seats = seats;
        this.createdAt = createdAt;
        this.paymentId = paymentId;
    }

    public Long getId() {
        return id;
    }

    public Event getEvent() {
        return event;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public int getSeats() {
        return seats;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getPaymentId() {
        return paymentId;
    }
}
