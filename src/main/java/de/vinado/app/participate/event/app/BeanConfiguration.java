package de.vinado.app.participate.event.app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class BeanConfiguration {

    @Bean
    CalendarUrl calendarUrl() {
        return new GoogleCalendarUrl();
    }
}
