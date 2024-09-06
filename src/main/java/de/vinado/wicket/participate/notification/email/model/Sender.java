package de.vinado.wicket.participate.notification.email.model;

import jakarta.mail.internet.InternetAddress;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.experimental.Accessors;

@Value
@Accessors(fluent = true)
@RequiredArgsConstructor
public class Sender {

    InternetAddress address;
}
