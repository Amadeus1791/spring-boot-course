package reviewservice.event;

import org.hibernate.validator.constraints.time.DurationMin;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import java.net.URI;
import java.time.Duration;

@ConfigurationProperties("events")
@Validated
public record EventsProperties(
        @DefaultValue("http://localhost:8084") URI baseUrl,
        @DefaultValue("2s") @DurationMin(millis = 1) Duration timeout) {
}
