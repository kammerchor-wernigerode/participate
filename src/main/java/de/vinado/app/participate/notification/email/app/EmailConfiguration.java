package de.vinado.app.participate.notification.email.app;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(SenderProperties.class)
class EmailConfiguration {
}
