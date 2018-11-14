package de.vinado.wicket.participate.services;

import de.vinado.wicket.participate.model.email.MailData;
import freemarker.template.TemplateException;
import org.springframework.mail.javamail.MimeMessagePreparator;

import javax.mail.internet.MimeMessage;
import java.io.IOException;

/**
 * Mail service provides several functions to send with a single plaintext email, a single multipart email, multiple
 * plaintext emails and multiple multipart emails.
 *
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public interface EmailService {

    /**
     * Sends an email with information in {@link MailData}.
     *
     * @param mailData the {@link MailData}
     */
    void send(MailData mailData);

    /**
     * Sends an email with information from {@link MailData} and with the name of the template file. Template files are
     * typically stored under {@code classpath:templates}.
     *
     * @param mailData         the {@link MailData}
     * @param templateFileName the template file name
     * @param html             whether template files is in HTML syntax
     */
    void send(MailData mailData, String templateFileName, boolean html);

    /**
     * Sends an email with information from {@link MailData} and with the name of the files to the plaintext and HTML
     * templates. Template files are typically stored under {@code classpath:templates}.
     *
     * @param mailData                  the {@link MailData}
     * @param plaintextTemplateFileName the plaintext template file names
     * @param htmlTemplateFileName      the HTML template file names
     * @throws IOException       if the template files could not be parsed
     * @throws TemplateException if the template files could not be found
     */
    void send(MailData mailData, String plaintextTemplateFileName, String htmlTemplateFileName) throws IOException, TemplateException;

    /**
     * Sends multiple emails prepared by {@link MimeMessagePreparator message preparators}.
     *
     * @param mimeMessagePreparators the {@link MimeMessagePreparator message preparators}
     * @see org.springframework.mail.javamail.JavaMailSender#send(MimeMessage...)
     */
    void send(MimeMessagePreparator... mimeMessagePreparators);

    /**
     * Returns preconfigured MimeMessagePrparator with multipart emails
     *
     * @param mailData                  the {@link MailData}
     * @param plaintextTemplateFileName the plaintext template file names
     * @param htmlTemplateFileName      the HTML template file names
     * @return the configured  {@link MimeMessagePreparator}
     * @throws IOException       if the template files could not be parsed
     * @throws TemplateException if the template files could not be found
     */
    MimeMessagePreparator getMimeMessagePreparator(MailData mailData, String plaintextTemplateFileName, String htmlTemplateFileName) throws IOException, TemplateException;

    /**
     * Returns preconfigured MimeMessagePreparator. The FreeMarker template file name may be null.
     *
     * @param mailData         the {@link MailData}
     * @param templateFileName the template file name
     * @param html             whether template files is in HTML syntax
     * @return the configured  {@link MimeMessagePreparator}
     */
    MimeMessagePreparator getMimeMessagePreparator(MailData mailData, String templateFileName, boolean html);
}
