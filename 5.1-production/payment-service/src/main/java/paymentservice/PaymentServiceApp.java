package paymentservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * A stand-in for a real payment service. Not part of the course content:
 * start it and leave it running.
 */
@SpringBootApplication
public class PaymentServiceApp {

    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceApp.class, args);
    }
}
