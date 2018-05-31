package de.vinado.wicket.participate.service;

import de.vinado.wicket.participate.ParticipateApplication;
import de.vinado.wicket.participate.data.email.EmailAttachment;
import de.vinado.wicket.participate.data.email.MailData;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.TransformerUtils;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.mail.internet.InternetAddress;
import javax.mail.util.ByteArrayDataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The service takes care of Emails.
 *
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
@Service
public class EmailService {

    private final static Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    @Qualifier("freeMarkerConfiguration")
    private Configuration freeMarkerConfig;

    /**
     * Sends your Email with the given data.
     *
     * @param mailData         {@link MailData}
     * @param templateFileName Template name
     */
    public void sendMail(final MailData mailData, final String templateFileName) throws MailException {
        javaMailSender.send(getMimeMessagePreparator(mailData, templateFileName));
    }

    public void sendMail(final MailData mailData) {
        javaMailSender.send(mimeMessage -> {
            final List<String> recipientList = new ArrayList<>(CollectionUtils.collect(
                    mailData.getRecipients(),
                    TransformerUtils.invokerTransformer("getEmail")));
            final String[] recipients = recipientList.toArray(new String[mailData.getRecipients().size()]);

            if (recipientList.isEmpty()) {
                LOGGER.error("Recipient is not set.");
                throw new WicketRuntimeException("Recipient is not set.");
            }

            final MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            helper.setSubject(mailData.getSubject());
            helper.setFrom(new InternetAddress(ParticipateApplication.get().getApplicationProperties().getMail().getSender(),
                    ParticipateApplication.get().getApplicationName()));
            helper.setTo(recipients);
            helper.setText(mailData.getMessage());
        });
    }

    public void sendMail(final MimeMessagePreparator... messagePreparators) {
        javaMailSender.send(messagePreparators);
    }

    /**
     * Prepares the mail for submitting.
     *
     * @param mailData         {@link MailData}
     * @param templateFileName Template file
     *
     * @return The actual message as {@link MimeMessagePreparator}
     */
    public MimeMessagePreparator getMimeMessagePreparator(final MailData mailData, final String templateFileName) {
        return mimeMessage -> {
            if (Strings.isEmpty(mailData.getRecipient())) {
                LOGGER.error("Recipient is not set.");
                throw new WicketRuntimeException("Recipient is not set.");
            }

            final MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setSubject(mailData.getSubject());
            helper.setFrom(new InternetAddress(
                    Strings.isEmpty(mailData.getSender())
                            ? ParticipateApplication.get().getApplicationProperties().getMail().getSender() : mailData.getSender(),
                    Strings.isEmpty(mailData.getSenderName())
                            ? ParticipateApplication.get().getApplicationName() : mailData.getSenderName()));
            helper.setTo(mailData.getRecipient());
            final EmailAttachment attachment = mailData.getAttachment();
            if (null != attachment) {
                helper.addAttachment(attachment.getName(), new ByteArrayDataSource(attachment.getInputStream(),
                        attachment.getMimeType()));
            }
            helper.setText(getFreeMarkerTemplateContent(mailData.getData(), templateFileName), true);
            if (null != mailData.getData().get("replyTo") && ((boolean) mailData.getData().get("replyTo")))
                helper.setReplyTo(new InternetAddress((String) mailData.getData().get("email"), mailData.getSenderName(), "UTF-8"));
        };
    }

    /**
     * Takes care of the Freemarker configuration and applies the mapping onto the template
     *
     * @param model Template mapping
     *
     * @return The Email body as a String
     */
    private String getFreeMarkerTemplateContent(final Map<String, Object> model, final String templateFileName) {
        final StringBuffer content = new StringBuffer();
        try {
            content.append(FreeMarkerTemplateUtils.processTemplateIntoString(freeMarkerConfig.getTemplate(templateFileName), model));
            return content.toString();
        } catch (final IOException | TemplateException e) {
            LOGGER.error("An exception occurred while processing a FreeMarker template.", e);
            return "";
        }
    }
}
