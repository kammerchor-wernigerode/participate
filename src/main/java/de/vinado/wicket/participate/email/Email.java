package de.vinado.wicket.participate.email;

import de.vinado.wicket.participate.configuration.ApplicationProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.lang.Nullable;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static lombok.AccessLevel.NONE;

/**
 * Mail data wrapper object
 *
 * @author Vincent Nadoll
 */
@Getter
@Setter
@NoArgsConstructor
public class Email {

    private static final String UTF_8 = StandardCharsets.UTF_8.name();

    // @formatter:off
    private @NonNull InternetAddress from;
    private Set<InternetAddress> to = new HashSet<>();
    private Set<InternetAddress> cc = new HashSet<>();
    private Set<InternetAddress> bcc = new HashSet<>();
    private InternetAddress replyTo;
    private @NonNull String subject;
    private @Nullable String message;
    private Set<EmailAttachment> attachments = new HashSet<>();
    private @Getter(NONE) Map<String, Object> data;
    // @formatter:on

    /**
     * Sets the sender email address.
     *
     * @param address the senders email address
     * @throws AddressException if the email address is malformed
     */
    public void setFrom(final String address) throws AddressException {
        this.from = new InternetAddress(address);
    }

    /**
     * Sets the sender name and email Address like <em>Vincent Nadoll &lt;me@vinado.de&gt;</em>.
     *
     * @param address  the senders email address
     * @param personal the senders name
     * @throws UnsupportedEncodingException if the encoding is not supported
     */
    public void setFrom(final String address, final String personal) throws UnsupportedEncodingException {
        this.from = new InternetAddress(address, personal, UTF_8);
    }

    /**
     * Adds a recipient.
     *
     * @param address the email address to add
     * @throws AddressException if the email address is malformed
     */
    public void addTo(final String address) throws AddressException {
        this.to.add(new InternetAddress(address));
    }

    /**
     * Adds a recipient with name and email address like <em>Vincent Nadoll &lt;me@vinado.de&gt;</em>.
     *
     * @param address  the email address to add
     * @param personal the name to add
     * @throws UnsupportedEncodingException if the encoding is not supported
     */
    public void addTo(final String address, final String personal) throws UnsupportedEncodingException {
        this.to.add(new InternetAddress(address, personal, UTF_8));
    }

    /**
     * Adds a CC recipient.
     *
     * @param address the email address to add
     * @throws AddressException if the email address is malformed
     */
    public void addCc(final String address) throws AddressException {
        this.cc.add(new InternetAddress(address));
    }

    /**
     * Adds a CC recipient with name and email address like <em>Vincent Nadoll &lt;me@vinado.de&gt;</em>.
     *
     * @param address  the email address to add
     * @param personal the name to add
     * @throws UnsupportedEncodingException if the encoding is not supported
     */
    public void addCc(final String address, final String personal) throws UnsupportedEncodingException {
        this.cc.add(new InternetAddress(address, personal, UTF_8));
    }

    /**
     * Adds a BCC recipient.
     *
     * @param address the email address to add
     * @throws AddressException if the email address is malformed
     */
    public void addBcc(final String address) throws AddressException {
        this.bcc.add(new InternetAddress(address));
    }

    /**
     * Adds a BCC recipient with name and email address like <em>Vincent Nadoll &lt;me@vinado.de&gt;</em>.
     *
     * @param address  the email address to add
     * @param personal the name to add
     * @throws UnsupportedEncodingException if the encoding is not supported
     */
    public void addBcc(final String address, final String personal) throws UnsupportedEncodingException {
        this.bcc.add(new InternetAddress(address, personal, UTF_8));
    }

    /**
     * Sets customizable data for usage with template engines.
     *
     * @param properties default configuration properties
     * @return key value map of objects
     */
    public Map<String, Object> getData(final ApplicationProperties properties) {
        final Map<String, Object> data = new HashMap<>();

        data.put("from", from);
        data.put("subject", subject);
        data.put("baseUrl", properties.getBaseUrl());
        data.put("footer", properties.getMail().getFooter());

        return data;
    }
}
