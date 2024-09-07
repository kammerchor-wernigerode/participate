package de.vinado.app.participate.notification.email.app;

import jakarta.mail.internet.InternetAddress;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("app.notification.email.sender")
@Getter
@Setter
public class SenderProperties {

    private InternetAddress from;

    private InternetAddress replyTo;
}
