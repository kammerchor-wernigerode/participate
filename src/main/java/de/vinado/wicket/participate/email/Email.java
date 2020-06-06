package de.vinado.wicket.participate.email;

import de.vinado.wicket.participate.configuration.ApplicationProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.lang.Nullable;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static lombok.AccessLevel.NONE;
import static lombok.AccessLevel.PROTECTED;

/**
 * Mail data wrapper object
 *
 * @author Vincent Nadoll
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = PROTECTED)
public class Email implements Serializable {

    private static final String UTF_8 = StandardCharsets.UTF_8.name();

    // @formatter:off
    private @NonNull InternetAddress from;
    private @Builder.Default Set<InternetAddress> to = new HashSet<>();
    private @Builder.Default Set<InternetAddress> cc = new HashSet<>();
    private @Builder.Default Set<InternetAddress> bcc = new HashSet<>();
    private InternetAddress replyTo;
    private @NonNull String subject;
    private @Getter(onMethod = @__(@Nullable)) @Setter(onParam = @__(@Nullable)) String message;
    private @Builder.Default Set<EmailAttachment> attachments = new HashSet<>();
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
     */
    @SneakyThrows
    public void setFrom(final String address, final String personal) {
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
     */
    @SneakyThrows
    public void addTo(final String address, final String personal) {
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
     */
    @SneakyThrows
    public void addCc(final String address, final String personal) {
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
     */
    @SneakyThrows
    public void addBcc(final String address, final String personal) {
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

    /**
     * Maps each recipient to its own email.
     *
     * @return stream of single recipient emails
     */
    public Stream<Email> toSingleRecipient() {
        return this.to.stream()
            .map(address -> Email.builder()
                .from(this.from)
                .to(Collections.singleton(address))
                .cc(this.cc)
                .bcc(this.bcc)
                .replyTo(this.replyTo)
                .subject(this.subject)
                .message(this.message)
                .attachments(this.attachments)
                .data(this.data)
                .build());
    }
}
