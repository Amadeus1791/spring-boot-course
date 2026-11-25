package eventservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.concurrent.ThreadLocalRandom;

// The other team's service, a stand-in for the real event service. Not part of the course content.
@RestController
public class EventController {

    record Event(Long id, String title, LocalDateTime startsAt) {
    }

    private final long delayMillis;
    private final double failureRate;

    EventController(@Value("${events.delay:0ms}") Duration delay,
                    @Value("${events.failure-rate:0.0}") double failureRate) {
        this.delayMillis = delay.toMillis();
        this.failureRate = failureRate;
    }

    @GetMapping("/api/events/{id}")
    ResponseEntity<Event> find(@PathVariable Long id) throws InterruptedException {
        Thread.sleep(delayMillis);
        if (ThreadLocalRandom.current().nextDouble() < failureRate) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        // Dates relative to today: event 1 is over, events 2 and 3 are still ahead.
        LocalDate today = LocalDate.now();
        if (id == 1) {
            return ResponseEntity.ok(new Event(1L, "Jazz Night",
                    LocalDateTime.of(today.minusDays(1), LocalTime.of(20, 0))));
        }
        if (id == 2) {
            return ResponseEntity.ok(new Event(2L, "Spring Boot Meetup",
                    LocalDateTime.of(today.plusDays(30), LocalTime.of(18, 30))));
        }
        if (id == 3) {
            return ResponseEntity.ok(new Event(3L, "String Quartet in Concert",
                    LocalDateTime.of(today.plusDays(60), LocalTime.of(19, 30))));
        }
        return ResponseEntity.notFound().build();
    }
}
