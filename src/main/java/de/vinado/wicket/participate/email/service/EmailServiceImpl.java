package de.vinado.wicket.participate.email.service;

import de.vinado.wicket.participate.configuration.ApplicationProperties;
import de.vinado.wicket.participate.email.Email;
import de.vinado.wicket.participate.email.EmailAttachment;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

import static de.vinado.wicket.participate.email.service.MultipartType.HTML;
import static de.vinado.wicket.participate.email.service.MultipartType.PLAIN;
import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Slf4j
@Primary
@Service
public class EmailServiceImpl implements EmailService {

    private static final String UTF_8 = StandardCharsets.UTF_8.name();

    private final JavaMailSender sender;
    private final TemplateService templateService;
    private final ApplicationProperties.Mail mailProperties;

    public EmailServiceImpl(JavaMailSender sender, TemplateService templateService, ApplicationProperties applicationProperties) {
        this.sender = sender;
        this.templateService = templateService;
        this.mailProperties = applicationProperties.getMail();
    }

    @Override
    public void send(Email email) {
        MimeMessagePreparator message = mimeMessage -> prepareMimeMessage(mimeMessage, email);
        log.debug("Email prepared and ready to ship");

        sender.send(message);
        logSuccess(1);
    }

    @Override
    public void send(Collection<Email> emails) {
        MimeMessagePreparator[] preparedMessages = emails.stream().map(email ->
            (MimeMessagePreparator) mimeMessage ->
                prepareMimeMessage(mimeMessage, email))
            .toArray(MimeMessagePreparator[]::new);
        log.debug("{} email(s) prepared and ready to ship", preparedMessages.length);

        sender.send(preparedMessages);
        logSuccess(preparedMessages.length);
    }

    /**
     * Sends a multipart email. Template files are typically stored under {@code classpath:templates}.
     *
     * @param email                     the email to send
     * @param plaintextTemplateFileName the plaintext template file name
     * @param htmlTemplateFileName      the HTML template file name
     */
    @Override
    public void send(Email email, String plaintextTemplateFileName, String htmlTemplateFileName) {
        MimeMessagePreparator message = mimeMessage -> prepareMimeMessage(mimeMessage, email, plaintextTemplateFileName, htmlTemplateFileName);
        log.debug("Email prepared and ready to ship");

        sender.send(message);
        logSuccess(1);
    }

    /**
     * Sends multiple multipart emails. Template files are typically stored under {@code classpath:templates}.
     *
     * @param emails                    a stream of emails to send
     * @param plaintextTemplateFileName the plaintext template file name
     * @param htmlTemplateFileName      the HTML template file name
     */
    @Override
    public void send(Collection<Email> emails, String plaintextTemplateFileName, String htmlTemplateFileName) {
        MimeMessagePreparator[] preparedMessages = emails.stream().map(email ->
            (MimeMessagePreparator) mimeMessage ->
                prepareMimeMessage(mimeMessage, email, plaintextTemplateFileName, htmlTemplateFileName))
            .toArray(MimeMessagePreparator[]::new);
        log.debug("{} email(s) prepared and ready to ship", preparedMessages.length);

        sender.send(preparedMessages);
        logSuccess(preparedMessages.length);
    }

    /**
     * Maps the information of an email onto a {@link MimeMessage} object.
     *
     * @param mimeMessage the mime message to map on
     * @param email       the email to map
     * @throws MessagingException if information could not be mapped
     */
    private void prepareMimeMessage(MimeMessage mimeMessage, Email email) throws MessagingException {
        boolean multipart = null != email.getAttachments() && !email.getAttachments().isEmpty();
        MimeMessageHelper helper = newMimeMessageHelper(email, mimeMessage, multipart);

        helper.setText(email.getMessage());
    }

    /**
     * Maps information of a multipart email onto a {@link MimeMessage} object.
     *
     * @param mimeMessage      the object to map on
     * @param email            the email to map
     * @param templateFileName the template file name
     * @param html             whether template files is in HTML syntax
     * @throws MessagingException           if information could not be mapped
     * @throws UnsupportedEncodingException if the encoding is not supported
     * @throws IOException                  if the template could not be parsed
     */
    private void prepareMimeMessage(MimeMessage mimeMessage, Email email, String templateFileName,
                                    boolean html)
        throws MessagingException, IOException, TemplateException {
        boolean multipart = (null != email.getAttachments() && !email.getAttachments().isEmpty())
            || html && isNotEmpty(email.getMessage());
        MimeMessageHelper helper = newMimeMessageHelper(email, mimeMessage, multipart);

        String text = templateService.processTemplate(templateFileName, email.getData(), html ? HTML : PLAIN);
        log.debug("Processed {} text email templates.", (html ? HTML : PLAIN).toString().toLowerCase());

        if (html) {
            if (isEmpty(email.getMessage())) {
                log.info("The email contains only HTML text and does not provide a fallback plaintext part");
                helper.setText(text, true);
            } else {
                helper.setText(email.getMessage(), text);
            }
        } else {
            helper.setText(text);
        }
    }

    /**
     * Maps information of a multipart email onto a {@link MimeMessage} object. {@link Email#getMessage()} is ignored.
     *
     * @param mimeMessage               the object to map on
     * @param email                     the email to map
     * @param plaintextTemplateFileName the plaintext template file name
     * @param htmlTemplateFileName      the HTML template file name
     * @throws MessagingException           if information could not be mapped
     * @throws UnsupportedEncodingException if the encoding is not supported
     * @throws IOException                  if the template could not be parsed
     */
    private void prepareMimeMessage(MimeMessage mimeMessage, Email email,
                                    String plaintextTemplateFileName, String htmlTemplateFileName)
        throws MessagingException, IOException, TemplateException {
        if (StringUtils.isBlank(plaintextTemplateFileName)) {
            prepareMimeMessage(mimeMessage, email, htmlTemplateFileName, true);
        } else if (StringUtils.isBlank(htmlTemplateFileName)) {
            prepareMimeMessage(mimeMessage, email, plaintextTemplateFileName, false);
        } else if (StringUtils.isNotBlank(plaintextTemplateFileName) && StringUtils.isNotBlank(htmlTemplateFileName)) {
            MimeMessageHelper helper = newMimeMessageHelper(email, mimeMessage, true);

            String htmlText = templateService.processTemplate(htmlTemplateFileName, email.getData(), HTML);
            String plainText = templateService.processTemplate(plaintextTemplateFileName, email.getData(), PLAIN);
            log.debug("Processed HTML and plain text email templates.");

            helper.setText(plainText, htmlText);
        } else {
            throw new IllegalArgumentException("Neither plaintext nor HTML template is set. Make sure at least one is not blank.");
        }
    }

    /**
     * Maps information of an email object to a new {@link MimeMessageHelper}. {@link MimeMessageHelper#setText} is not
     * called.
     *
     * @param email       the email to map
     * @param mimeMessage the object to map on
     * @param multipart   whether the email will contain multiple parts like HTML part and alternative plaintext part.
     * @return new and configured helper object
     * @throws MessagingException if multipart creation failed
     */
    private MimeMessageHelper newMimeMessageHelper(Email email, MimeMessage mimeMessage,
                                                   boolean multipart) throws MessagingException {
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, multipart, UTF_8);
        log.debug("A new email helper object is created. Start to map" + (multipart ? " multipart " : " ") + "email information to helper.");

        helper.setFrom(email.getFrom());
        helper.setTo(email.getTo().toArray(new InternetAddress[0]));
        helper.setCc(email.getCc().toArray(new InternetAddress[0]));
        helper.setBcc(email.getBcc().toArray(new InternetAddress[0]));
        if (null != email.getReplyTo()) helper.setReplyTo(email.getReplyTo());
        helper.setSubject(email.getSubject());

        if (null != email.getReplyTo()) {
            helper.setReplyTo(email.getReplyTo());
        } else if (null != mailProperties.getReplyTo()) {
            helper.setReplyTo(mailProperties.getReplyTo());
        }

        if (multipart) {
            int i = 0;
            for (EmailAttachment attachment : email.getAttachments()) {
                helper.addAttachment(attachment.getName(), attachment.getInputStream(), attachment.getMimeType().toString());
                i++;
            }
            log.debug("Attached {} objects to mime message", i);
        }

        return helper;
    }

    private void logSuccess(int amount) {
        if (0 != amount) {
            log.info("Sent {} email{}", amount, 1 == amount ? "" : "s");
        }
    }
}
