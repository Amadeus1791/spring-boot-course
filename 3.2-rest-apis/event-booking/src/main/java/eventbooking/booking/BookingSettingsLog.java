package eventbooking.booking;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

// Shows the active booking settings once at startup.
@Component
class BookingSettingsLog implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(BookingSettingsLog.class);

    private final BookingProperties properties;

    BookingSettingsLog(BookingProperties properties) {
        this.properties = properties;
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("At most {} seats per booking", properties.maxSeats());
    }
}
