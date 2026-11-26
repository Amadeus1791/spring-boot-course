package eventbooking.payment;

import org.hibernate.validator.constraints.time.DurationMin;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import java.net.URI;
import java.time.Duration;

@ConfigurationProperties("payment")
@Validated
public record PaymentProperties(
        @DefaultValue("http://localhost:8081") URI baseUrl,
        @DefaultValue("3s") @DurationMin(millis = 1) Duration timeout) {
}
