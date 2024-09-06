package de.vinado.app.participate.notification.email.model;

import jakarta.mail.internet.InternetAddress;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.Accessors;

import java.util.Optional;
import java.util.StringJoiner;

@Value
@Accessors(fluent = true)
@RequiredArgsConstructor
public class Sender {

    InternetAddress address;
    InternetAddress replyTo;

    public Optional<InternetAddress> replyTo() {
        return Optional.ofNullable(replyTo);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Sender.class.getSimpleName() + "[", "]")
            .add("address=" + address.toUnicodeString())
            .add("replyTo=" + replyTo.toUnicodeString())
            .toString();
    }
}
