package eventbooking.booking;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record BookRequest(@NotBlank @Email String customerEmail, @Positive int seats) {
}
