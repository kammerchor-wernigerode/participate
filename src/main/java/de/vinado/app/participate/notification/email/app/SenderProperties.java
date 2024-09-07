package de.vinado.app.participate.notification.email.app;

import jakarta.mail.internet.InternetAddress;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties("app.notification.email.sender")
@Getter
@Setter
@Validated
public class SenderProperties {

    /**
     * Sender's email address. In other words: the address this application sends emails from.
     */
    @NonNull
    @NotNull
    private InternetAddress from;

    /**
     * If set, this address will be used as the reply-to address in all emails sent by this application.
     */
    @Nullable
    private InternetAddress replyTo;
}
