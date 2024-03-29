package de.vinado.wicket.participate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {
    "de.vinado.wicket.participate",
    "de.vinado.app.participate"
})
@EnableScheduling
@EnableConfigurationProperties
public class Participate {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Participate.class, args);
    }
}
