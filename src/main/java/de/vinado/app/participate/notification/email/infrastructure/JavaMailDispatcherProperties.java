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

    @Positive
    private int concurrentTransmissions;

    @NonNull
    @NotNull
    private Duration shutdownGracePeriod;

    @Positive
    private int recipientThreshold;
}
