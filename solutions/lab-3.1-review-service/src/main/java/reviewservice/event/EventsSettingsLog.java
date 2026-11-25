package reviewservice.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

// Shows the active settings of the event service once at startup.
@Component
class EventsSettingsLog implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(EventsSettingsLog.class);

    private final EventsProperties properties;

    EventsSettingsLog(EventsProperties properties) {
        this.properties = properties;
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("Event service at {}, timeout {}", properties.baseUrl(), properties.timeout());
    }
}
