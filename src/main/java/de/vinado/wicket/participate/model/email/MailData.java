package de.vinado.wicket.participate.model.email;

import de.vinado.wicket.participate.ParticipateApplication;
import de.vinado.wicket.participate.configuration.ApplicationProperties;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.validation.constraints.NotNull;
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
public class MailData implements Serializable {

    private static final String UTF_8 = StandardCharsets.UTF_8.name();

    @NotNull
    private InternetAddress from;

    @NotNull
    private List<InternetAddress> to = new ArrayList<>();

    private String replyTo;

    private String subject;

    private String message;

    private List<EmailAttachment> attachments = new ArrayList<>();

    public MailData() {
    }

    public MailData(final MailData copy) {
        this.from = copy.getFrom();
        this.to = copy.getTo();
        this.replyTo = copy.getReplyTo();
        this.subject = copy.getSubject();
        this.message = copy.getMessage();
        this.attachments = copy.getAttachments();
    }

    public InternetAddress getFrom() {
        return from;
    }

    public void setFrom(final String address) throws AddressException {
        this.from = new InternetAddress(address);
    }

    public void setFrom(final InternetAddress from) {
        this.from = from;
    }

    public void setFrom(final String address, final String personal) {
        try {
            this.from = new InternetAddress(address, personal, UTF_8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public List<InternetAddress> getTo() {
        return to;
    }

    public void setTo(final List<InternetAddress> to) {
        this.to = to;
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

    public String getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(final String replyTo) {
        this.replyTo = replyTo;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(final String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public List<EmailAttachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(final List<EmailAttachment> attachments) {
        this.attachments = attachments;
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
