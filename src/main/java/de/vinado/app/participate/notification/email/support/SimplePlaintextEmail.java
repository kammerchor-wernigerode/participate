package de.vinado.app.participate.notification.email.support;

import de.vinado.app.participate.notification.email.model.Email;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Optional;

@RequiredArgsConstructor
@Accessors(fluent = true)
@EqualsAndHashCode
@ToString
public class SimplePlaintextEmail implements Email {

    @Getter
    private final String subject;
    private final String text;

    @Override
    public Optional<String> textContent() {
        return Optional.of(text);
    }
}
