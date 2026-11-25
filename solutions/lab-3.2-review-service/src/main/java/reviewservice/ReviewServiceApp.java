package reviewservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ReviewServiceApp {

    public static void main(String[] args) {
        SpringApplication.run(ReviewServiceApp.class, args);
    }
}
