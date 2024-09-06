package de.vinado.wicket.participate.notification.email.model;

import java.util.Optional;

public interface Email {

    String subject();

    default Optional<String> textContent() {
        return Optional.empty();
    }

    default Optional<String> htmlContent() {
        return Optional.empty();
    }
}
