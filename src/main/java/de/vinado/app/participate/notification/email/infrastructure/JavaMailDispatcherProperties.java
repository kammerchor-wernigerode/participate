package de.vinado.app.participate.notification.email.infrastructure;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties("app.notification.email.dispatch")
@Getter
@Setter
class JavaMailDispatcherProperties {

    private int concurrentTransmissions;

    @NonNull
    private Duration shutdownGracePeriod;
}
