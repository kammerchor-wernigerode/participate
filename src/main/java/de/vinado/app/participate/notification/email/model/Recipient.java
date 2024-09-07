package de.vinado.app.participate.notification.email.model;

import de.vinado.wicket.participate.model.Person;
import jakarta.mail.Message.RecipientType;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Accessors(fluent = true)
@EqualsAndHashCode
public class Recipient {

    private final InternetAddress address;

    private final RecipientType type;

    public static Recipient.To to(String address) throws AddressException {
        return to(new InternetAddress(address));
    }

    public static Recipient.To to(InternetAddress address) {
        return new Recipient.To(address);
    }

    @SneakyThrows
    public static Recipient.To to(Person person) {
        return to(new InternetAddress(person.getEmail(), person.getDisplayName(), "UTF-8"));
    }

    public static Recipient.To[] to(String... addresses) throws AddressException {
        List<Recipient.To> recipients = new ArrayList<>(addresses.length);
        for (String address : addresses) {
            recipients.add(to(address));
        }
        return recipients.toArray(new Recipient.To[0]);
    }

    public static Recipient.To[] to(InternetAddress... addresses) {
        return Arrays.stream(addresses)
            .map(Recipient::to)
            .toArray(Recipient.To[]::new);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Recipient.class.getSimpleName() + "[", "]")
            .add("address=" + address.toUnicodeString())
            .add("type=" + type)
            .toString();
    }


    public static class To extends Recipient {

        private To(InternetAddress address) {
            super(address, RecipientType.TO);
        }
    }
}
