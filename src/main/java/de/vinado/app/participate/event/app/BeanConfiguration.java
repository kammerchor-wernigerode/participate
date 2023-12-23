package de.vinado.app.participate.event.app;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(GoogleCalendarUrlProperties.class)
class BeanConfiguration {

    @Bean
    CalendarUrl calendarUrl(GoogleCalendarUrlProperties properties) {
        return new GoogleCalendarUrl(properties);
    }
}
