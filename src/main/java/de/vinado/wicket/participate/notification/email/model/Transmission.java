package de.vinado.wicket.participate.notification.email.model;

import java.util.Arrays;
import java.util.StringJoiner;

public record Transmission(Sender sender, Recipient... recipients) {

    @Override
    public String toString() {
        return new StringJoiner(", ", Transmission.class.getSimpleName() + "[", "]")
            .add("sender=" + sender)
            .add("recipients=" + Arrays.toString(recipients))
            .toString();
    }
}
