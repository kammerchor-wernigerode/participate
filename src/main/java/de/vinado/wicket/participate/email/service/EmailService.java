package de.vinado.wicket.participate.email.service;

import de.vinado.wicket.participate.email.Email;

import java.util.stream.Stream;

/**
 * Provides several functions to send single plaintext email, a single multipart email, multiple plaintext emails and
 * multiple multipart emails.
 *
 * @author Vincent Nadoll
 */
public interface EmailService {

    /**
     * Sends an email.
     *
     * @param email the email to send
     */
    void send(Email email);

    /**
     * Sends multiple emails.
     *
     * @param emails a stream of emails to send
     */
    default void send(Stream<Email> emails) {
        emails.forEach(this::send);
    }

    /**
     * Sends a multipart email. Template files are typically stored in {@code classpath:templates}.
     *
     * @param email            the email to send
     * @param templateFileName the template file name
     * @param html             whether template files is in HTML syntax
     */
    void send(Email email, String templateFileName, boolean html);

    /**
     * Sends multiple multipart emails. Template files are typically stored under {@code classpath:templates}.
     *
     * @param emails           a stream of emails to send
     * @param templateFileName the template file name
     * @param html             whether template files is in HTML syntax
     */
    default void send(Stream<Email> emails, String templateFileName, boolean html) {
        emails.forEach(email -> send(email, templateFileName, html));
    }

    /**
     * Sends a multipart email. Template files are typically stored under {@code classpath:templates}.
     *
     * @param email                     the email to send
     * @param plaintextTemplateFileName the plaintext template file name
     * @param htmlTemplateFileName      the HTML template file name
     */
    void send(Email email, String plaintextTemplateFileName, String htmlTemplateFileName);

    /**
     * Sends multiple multipart emails. Template files are typically stored under {@code classpath:templates}.
     *
     * @param emails                    a stream of emails to send
     * @param plaintextTemplateFileName the plaintext template file name
     * @param htmlTemplateFileName      the HTML template file name
     */
    default void send(Stream<Email> emails, String plaintextTemplateFileName, String htmlTemplateFileName) {
        emails.forEach(email -> send(email, plaintextTemplateFileName, htmlTemplateFileName));
    }
}
