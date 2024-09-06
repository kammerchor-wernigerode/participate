package de.vinado.wicket.participate.notification.email.app;

import jakarta.mail.internet.InternetAddress;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("app.notification.email")
@Getter
@Setter
public class EmailProperties {

    private InternetAddress sender;

    private InternetAddress replyTo;
}
