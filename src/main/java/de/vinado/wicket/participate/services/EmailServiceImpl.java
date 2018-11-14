package de.vinado.wicket.participate.services;

import de.vinado.wicket.participate.model.email.EmailAttachment;
import de.vinado.wicket.participate.model.email.MailData;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.mail.internet.InternetAddress;
import javax.mail.util.ByteArrayDataSource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Locale;

/**
 * Mail service implementations provides several functions to send with a single plaintext email, a single multipart
 * email, multiple plaintext emails and multiple multipart emails.
 *
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
@Primary
@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EmailServiceImpl implements EmailService {

    private static final String UTF_8 = StandardCharsets.UTF_8.name();

    private final JavaMailSender sender;
    private final Configuration freeMarker;

    /**
     * {@inheritDoc}
     * <br>
     * Sends plaintext email
     *
     * @param mailData the {@link MailData}
     */
    @Override
    public void send(final MailData mailData) {
        sender.send(getMimeMessagePreparator(mailData, null, false));
    }

    /**
     * {@inheritDoc}
     * <br>
     * Sends multipart email
     *
     * @param mailData         {@link MailData}
     * @param templateFileName Template file name located in 'templates/'
     * @param html             Whether template is html
     */
    @Override
    public void send(final MailData mailData, final String templateFileName, final boolean html) {
        sender.send(getMimeMessagePreparator(mailData, templateFileName, html));
    }

    /**
     * {@inheritDoc}
     * <br>
     * Sends multipart email with plaintext template file and html template file
     *
     * @param mailData                  {@link MailData}
     * @param plaintextTemplateFileName FreeMarker plaintext template file located in 'templates/'
     * @param htmlTemplateFileName      FreeMarker html template file located in 'templates/'
     * @throws IOException       Cannot parse template file
     * @throws TemplateException Template file does not exist
     */
    @Override
    public void send(final MailData mailData, final String plaintextTemplateFileName,
                     final String htmlTemplateFileName) throws IOException, TemplateException {
        sender.send(getMimeMessagePreparator(mailData, plaintextTemplateFileName, htmlTemplateFileName));
    }

    /**
     * {@inheritDoc}
     * <br>
     * Sends multiple plaintext emails
     * <br>
     * <p>Usage: Define your own Array of MimeMessagePreparators. Typically used to send different emails with different
     * data and/or template files</p>
     *
     * @param mimeMessagePreparators Array of {@link MimeMessagePreparator message preparators}
     */
    @Override
    public void send(final MimeMessagePreparator... mimeMessagePreparators) {
        sender.send(mimeMessagePreparators);
    }

    /**
     * Sends multiple plaintext emails
     * <br>
     * <p>Usage: Define your own Array of MimeMessagePreparators. Typically used to send different emails with different
     * data and/or template files.</p>
     *
     * @param mimeMessagePreparators Collection of {@link MimeMessagePreparator message preparators}
     * @see #getMimeMessagePreparator(MailData, String, boolean)
     */
    private void send(final Collection<MimeMessagePreparator> mimeMessagePreparators) {
        sender.send(mimeMessagePreparators.toArray(new MimeMessagePreparator[0]));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MimeMessagePreparator getMimeMessagePreparator(final MailData mailData,
                                                          final String plaintextTemplateFileName,
                                                          final String htmlTemplateFileName) throws IOException,
        TemplateException {
        mailData.setMessage(FreeMarkerTemplateUtils.processTemplateIntoString(
            freeMarker.getTemplate(plaintextTemplateFileName, Locale.getDefault()), mailData.getData()));

        return getMimeMessagePreparator(mailData, htmlTemplateFileName, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MimeMessagePreparator getMimeMessagePreparator(final MailData mailData, final String templateFileName,
                                                          final boolean html) {
        return mimeMessage -> {
            final MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, !Strings.isEmpty(templateFileName),
                UTF_8);
            helper.setFrom(mailData.getFrom());
            helper.setTo(mailData.getTo().toArray(new InternetAddress[0]));
            helper.setSubject(mailData.getSubject());

            if (!Strings.isEmpty(templateFileName) && !Strings.isEmpty(mailData.getMessage())) {
                helper.setText(
                    mailData.getMessage(),
                    FreeMarkerTemplateUtils.processTemplateIntoString(
                        freeMarker.getTemplate(templateFileName, Locale.getDefault()),
                        mailData.getData()
                    )
                );
            } else if (!Strings.isEmpty(mailData.getMessage())) {
                helper.setText(mailData.getMessage());
            } else if (!Strings.isEmpty(templateFileName)) {
                if (html) log.warn("You may provide a plaintext version of your email too");
                helper.setText(FreeMarkerTemplateUtils.processTemplateIntoString(
                    freeMarker.getTemplate(templateFileName, Locale.getDefault()), mailData.getData()), html);
            } else {
                log.error("Error no mail body provided",
                    new NullPointerException("Neither 'templateFileName' nor 'MailData#message' cannot be null"));
            }

            if (!Strings.isEmpty(mailData.getReplyTo())) {
                helper.setReplyTo(mailData.getReplyTo());
            }

            for (EmailAttachment attachment : mailData.getAttachments()) {
                helper.addAttachment(
                    attachment.getName(),
                    new ByteArrayDataSource(attachment.getInputStream(), attachment.getMediaType())
                );
            }
        };
    }
}
