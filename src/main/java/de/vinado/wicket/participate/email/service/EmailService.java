package de.vinado.wicket.participate.email.service;

import de.vinado.wicket.participate.email.Email;

import java.util.Collection;

public interface EmailService {

    void send(Email email);

    default void send(Collection<Email> emails) {
        emails.forEach(this::send);
    }

    void send(Email email, String plaintextTemplateFileName, String htmlTemplateFileName);

    default void send(Collection<Email> emails, String plaintextTemplateFileName, String htmlTemplateFileName) {
        emails.forEach(email -> send(email, plaintextTemplateFileName, htmlTemplateFileName));
    }
}
