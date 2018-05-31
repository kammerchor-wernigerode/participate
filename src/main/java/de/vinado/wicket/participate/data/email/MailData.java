package de.vinado.wicket.participate.data.email;

import de.vinado.wicket.participate.ParticipateApplication;
import de.vinado.wicket.participate.data.Person;
import org.apache.wicket.util.string.Strings;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class MailData implements Serializable {

    private String sender;

    private String senderName;

    private String recipient;

    private List<Person> recipients;

    private String subject;

    private String message;

    private EmailAttachment attachment;

    public MailData() {

    }

    public MailData(final String sender, final String recipient, final String subject) {
        this(sender, null, recipient, subject);
    }

    public MailData(final String sender, final String senderName, final String recipient, final String subject) {
        this.sender = sender;
        this.senderName = senderName;
        this.recipient = recipient;
        this.subject = subject;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(final String sender) {
        this.sender = sender;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(final String senderName) {
        this.senderName = senderName;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(final String recipient) {
        this.recipient = recipient;
    }

    public List<Person> getRecipients() {
        return recipients;
    }

    public void setRecipients(final List<Person> recipients) {
        this.recipients = recipients;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(final String subject) {
        this.subject = subject;
    }

    public EmailAttachment getAttachment() {
        return attachment;
    }

    public void setAttachment(final EmailAttachment attachment) {
        this.attachment = attachment;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public Map<String, Object> getData() {
        final Map<String, Object> data = new HashMap<>();
        data.put("sender", sender);
        if (!Strings.isEmpty(senderName))
            data.put("senderName", senderName);
        if (!Strings.isEmpty(recipient))
            data.put("recipient", recipient);
        if (!Strings.isEmpty(subject))
            data.put("subject", subject);
        if (null != attachment) {
            data.put("attachment", attachment);
        }
        data.put("baseUrl", ParticipateApplication.get().getBaseUrl());
        if (!Strings.isEmpty(ParticipateApplication.get().getApplicationProperties().getMail().getFooter()))
            data.put("footer", ParticipateApplication.get().getApplicationProperties().getMail().getFooter());
        return data;
    }
}
