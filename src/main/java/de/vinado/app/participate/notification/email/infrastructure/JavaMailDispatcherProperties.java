package de.vinado.app.participate.notification.email.infrastructure;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@ConfigurationProperties("app.notification.email.dispatch")
@Getter
@Setter
@Validated
class JavaMailDispatcherProperties {

    /**
     * Amount of concurrent SMTP sessions.
     */
    @Positive
    private int concurrentTransmissions;

    /**
     * Amount of time the dispatcher waits for running transmissions before shutting down forcefully.
     */
    @NonNull
    @NotNull
    private Duration shutdownGracePeriod;

    /**
     * Maximum amount of recipients per transmission.
     */
    @Positive
    private int recipientThreshold;
}
