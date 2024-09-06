package de.vinado.wicket.participate.notification.email.infrastructure;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties("app.notification.email.dispatch")
@Getter
@Setter
public class JavaMailDispatcherProperties {

    private int concurrentSessions;

    @NonNull
    private Duration shutdownGracePeriod;
}
