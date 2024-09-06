package de.vinado.wicket.participate.notification.email.model;

import jakarta.mail.internet.InternetAddress;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.Accessors;

import java.util.Optional;

@Value
@Accessors(fluent = true)
@RequiredArgsConstructor
public class Sender {

    InternetAddress address;
    InternetAddress replyTo;

    public Optional<InternetAddress> replyTo() {
        return Optional.ofNullable(replyTo);
    }
}
