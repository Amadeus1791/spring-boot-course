package eventbooking.booking;

import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties("booking")
@Validated
public record BookingProperties(
        @DefaultValue("6") @Positive int maxSeats) {
}
