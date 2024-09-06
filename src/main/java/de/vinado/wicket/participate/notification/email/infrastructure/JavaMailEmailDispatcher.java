package de.vinado.wicket.participate.notification.email.infrastructure;

import de.vinado.wicket.participate.notification.email.model.Email;
import de.vinado.wicket.participate.notification.email.model.EmailDispatcher;
import de.vinado.wicket.participate.notification.email.model.EmailException;
import de.vinado.wicket.participate.notification.email.model.Recipient;
import de.vinado.wicket.participate.notification.email.model.Transmission;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JavaMailEmailDispatcher implements EmailDispatcher {

    private final JavaMailSender sender;

    @Override
    public void dispatch(Email email, Transmission... transmissions) throws EmailException {
        try {
            for (Transmission transmission : transmissions) {
                dispatch(email, transmission);
            }
        } catch (Exception e) {
            throw new EmailException(e);
        }
    }

    public void dispatch(Email email, Transmission transmission) throws MessagingException {
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

        helper.setSubject(email.subject());
        helper.setFrom(transmission.sender().address());

        Optional<InternetAddress> replyTo = transmission.sender().replyTo();
        if (replyTo.isPresent()) {
            helper.setReplyTo(replyTo.get());
        }

        for (Recipient recipient : transmission.recipients()) {
            message.addRecipients(recipient.type(), new InternetAddress[]{recipient.address()});
        }

        Optional<String> text = email.textContent();
        if (text.isPresent()) {
            helper.setText(text.get(), false);
        }

        Optional<String> html = email.htmlContent();
        if (html.isPresent()) {
            helper.setText(html.get(), true);
        }

        Iterator<Email.Attachment> attachments = email.attachments().iterator();
        while (attachments.hasNext()) {
            Email.Attachment attachment = attachments.next();
            helper.addAttachment(attachment.name(), attachment, attachment.type().toString());
        }

        sender.send(message);
    }
}
