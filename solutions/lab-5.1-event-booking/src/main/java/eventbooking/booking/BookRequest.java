package eventbooking.booking;

import jakarta.validation.constraints.Positive;

// Since chapter 4.2 the customer is who is logged in: the body names only the seats.
public record BookRequest(@Positive int seats) {
}
