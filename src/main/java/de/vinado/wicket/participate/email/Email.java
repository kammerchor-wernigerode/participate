package de.vinado.wicket.participate.email;

import de.vinado.wicket.participate.model.Person;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.mail.internet.InternetAddress;

import static de.vinado.wicket.participate.email.InternetAddressFactory.create;
import static lombok.AccessLevel.PRIVATE;

@Getter
@AllArgsConstructor(access = PRIVATE)
public class Email implements Serializable {

    private final InternetAddress from;
    private Set<InternetAddress> to;
    private Set<InternetAddress> cc;
    private Set<InternetAddress> bcc;
    private InternetAddress replyTo;
    private String subject;
    private String message;
    private Set<EmailAttachment> attachments;
    private Map<String, Object> data;

    public Builder mutate() {
        return builder(from)
            .toAddresses(to)
            .ccAddresses(cc)
            .bccAddresses(bcc)
            .replyTo(replyTo)
            .subject(subject)
            .message(message)
            .attachments(attachments)
            .data(data);
    }

    protected void setTo(Collection<InternetAddress> addresses) {
        this.to = new HashSet<>(addresses);
    }

    protected void addTo(InternetAddress address) {
        this.to.add(address);
    }

    protected void setSubject(String subject) {
        this.data.put("subject", subject);
        this.subject = subject;
    }

    protected void setMessage(String message) {
        this.message = message;
    }

    /**
     * Maps each recipient to its own email.
     *
     * @return stream of single recipient emails
     */
    public Stream<Email> toSingleRecipient() {
        return this.to.stream()
            .map(address ->
                mutate()
                    .setTo(address)
                    .build()
            );
    }

    public static Builder builder(InternetAddress from) {
        return new Builder(from);
    }

    @RequiredArgsConstructor(access = PRIVATE)
    public static class Builder implements Cloneable, Serializable {

        private final InternetAddress from;
        private String subject;
        private Set<InternetAddress> to = new HashSet<>();
        private Set<InternetAddress> cc = new HashSet<>();
        private Set<InternetAddress> bcc = new HashSet<>();
        private InternetAddress replyTo;
        private String message;
        private Set<EmailAttachment> attachments = new HashSet<>();
        private Map<String, Object> data = new HashMap<>();

        Builder(Builder other) {
            Assert.notNull(other, "Other builder must not be null");

            this.from = other.from;
            this.subject = other.subject;
            this.to = other.to;
            this.cc = other.cc;
            this.bcc = other.bcc;
            this.replyTo = other.replyTo;
            this.message = other.message;
            this.attachments = other.attachments;
            this.data = other.data;
        }

        public Builder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public Builder to(String email, String personal) {
            this.to.add(InternetAddressFactory.create(email, personal));
            return this;
        }

        public Builder to(InternetAddress... addresses) {
            return toAddresses(Arrays.asList(addresses));
        }

        public Builder toAddresses(Collection<InternetAddress> addresses) {
            this.to.addAll(addresses);
            return this;
        }

        public Builder setTo(String email, String personal) {
            return setTo(InternetAddressFactory.create(email, personal));
        }

        public Builder setTo(InternetAddress... addresses) {
            return setToAddresses(Arrays.asList(addresses));
        }

        public Builder setToAddresses(Collection<InternetAddress> addresses) {
            this.to = new HashSet<>(addresses);
            return this;
        }

        public Builder to(Person... people) {
            return toPeople(Arrays.asList(people));
        }

        public Builder to(String email) {
            this.to.add(create(email));
            return this;
        }

        public Builder toPeople(Collection<? extends Person> people) {
            people.stream().map(InternetAddressFactory::create).forEach(addTo(this.to));
            return this;
        }

        public Builder cc(String email, String personal) {
            this.cc.add(InternetAddressFactory.create(email, personal));
            return this;
        }

        public Builder cc(Person... people) {
            return ccPeople(Arrays.asList(people));
        }

        public Builder cc(String email) {
            this.cc.add(create(email));
            return this;
        }

        public Builder ccPeople(Collection<? extends Person> people) {
            people.stream().map(InternetAddressFactory::create).forEach(addTo(this.cc));
            return this;
        }

        public Builder ccAddresses(Collection<InternetAddress> addresses) {
            this.cc.addAll(addresses);
            return this;
        }

        public Builder bcc(String email, String personal) {
            this.bcc.add(InternetAddressFactory.create(email, personal));
            return this;
        }

        public Builder bcc(Person... people) {
            return bccPeople(Arrays.asList(people));
        }

        public Builder bcc(String email) {
            this.bcc.add(create(email));
            return this;
        }

        public Builder bccPeople(Collection<? extends Person> people) {
            people.stream().map(InternetAddressFactory::create).forEach(addTo(this.bcc));
            return this;
        }

        public Builder bccAddresses(Collection<InternetAddress> addresses) {
            this.bcc.addAll(addresses);
            return this;
        }

        public Builder replyTo(InternetAddress address) {
            this.replyTo = address;
            return this;
        }

        public Builder replyTo(Person person) {
            this.replyTo = InternetAddressFactory.create(person);
            return this;
        }

        public Builder replyTo(String email) {
            this.replyTo = create(email);
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder attachments(EmailAttachment... attachments) {
            return attachments(Arrays.asList(attachments));
        }

        public Builder attachments(Collection<EmailAttachment> attachments) {
            Assert.notNull(attachments, "Email attachment collection must not be null");
            this.attachments.addAll(attachments);
            return this;
        }

        public Builder data(String key, Object value) {
            Assert.hasText(key, "Data key must not be null");
            this.data.put(key, value);
            return this;
        }

        public Builder data(Map<String, Object> data) {
            Assert.notNull(data, "Data must not be null");
            this.data.putAll(data);
            return this;
        }

        public Builder clone() {
            return new Builder(this);
        }

        public Email build() {
            Assert.notNull(from, "From address must not be null");

            data("from", from);
            data("subject", subject);

            return new Email(from, to, cc, bcc, replyTo, subject, message, attachments, data);
        }

        private static Consumer<InternetAddress> addTo(Collection<InternetAddress> field) {
            return field::add;
        }
    }
}
