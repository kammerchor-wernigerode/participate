package de.vinado.wicket.participate.model.email;

import de.vinado.wicket.participate.ParticipateApplication;
import de.vinado.wicket.participate.configuration.ApplicationProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mail data wrapper object
 *
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
@Getter
@Setter
@NoArgsConstructor
public class MailData implements Serializable {

    private static final String UTF_8 = StandardCharsets.UTF_8.name();

    private @NonNull InternetAddress from;
    private @NonNull List<InternetAddress> to = new ArrayList<>();
    private String replyTo;
    private String subject;
    private String message;
    private List<EmailAttachment> attachments = new ArrayList<>();

    public MailData(final MailData copy) {
        this.from = copy.getFrom();
        this.to = copy.getTo();
        this.replyTo = copy.getReplyTo();
        this.subject = copy.getSubject();
        this.message = copy.getMessage();
        this.attachments = copy.getAttachments();
    }

    public void setFrom(final String address) throws AddressException {
        this.from = new InternetAddress(address);
    }

    public void setFrom(final String address, final String personal) {
        try {
            this.from = new InternetAddress(address, personal, UTF_8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void addTo(final InternetAddress to) {
        this.to.add(to);
    }

    public void addTo(final String address) throws AddressException {
        this.to.add(new InternetAddress(address));
    }

    public void addTo(final String address, final String personal) {
        try {
            this.to.add(new InternetAddress(address, personal, UTF_8));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void addAttachment(final EmailAttachment attachment) {
        this.attachments.add(attachment);
    }

    /**
     * Override this method and add some extra data. This method is useful, when you work with FreeMarker template
     * engine and HTML Mails
     *
     * @return Map of tagged objects
     */
    public Map<String, Object> getData() {
        final Map<String, Object> data = new HashMap<>();
        final ApplicationProperties.Mail mailProperties = ParticipateApplication.get().getApplicationProperties().getMail();

        data.put("from", from);
        data.put("subject", subject);
        data.put("baseUrl", ParticipateApplication.get().getBaseUrl());
        data.put("footer", mailProperties.getFooter());


        return data;
    }
}
