package eventbooking.payment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

// Shows the active payment settings once at startup.
@Component
class PaymentSettingsLog implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(PaymentSettingsLog.class);

    private final PaymentProperties properties;

    PaymentSettingsLog(PaymentProperties properties) {
        this.properties = properties;
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("Payment service at {}, timeout {}", properties.baseUrl(), properties.timeout());
    }
}
