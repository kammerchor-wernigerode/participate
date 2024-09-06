package de.vinado.wicket.participate.notification.email.model;

import jakarta.mail.Message.RecipientType;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public static Recipient.Cc cc(String address) throws AddressException {
        return cc(new InternetAddress(address));
    }

    public static Recipient.Cc cc(InternetAddress address) {
        return new Recipient.Cc(address);
    }

    public static Recipient.Cc[] cc(String... addresses) throws AddressException {
        List<Recipient.Cc> recipients = new ArrayList<>(addresses.length);
        for (String address : addresses) {
            recipients.add(cc(address));
        }
        return recipients.toArray(new Recipient.Cc[0]);
    }

    public static Recipient.Cc[] cc(InternetAddress... addresses) {
        return Arrays.stream(addresses)
            .map(Recipient::cc)
            .toArray(Recipient.Cc[]::new);
    }

    public static Recipient.Bcc bcc(String address) throws AddressException {
        return bcc(new InternetAddress(address));
    }

    public static Recipient.Bcc bcc(InternetAddress address) {
        return new Recipient.Bcc(address);
    }

    public static Recipient.Bcc[] bcc(String... addresses) throws AddressException {
        List<Recipient.Bcc> recipients = new ArrayList<>(addresses.length);
        for (String address : addresses) {
            recipients.add(bcc(address));
        }
        return recipients.toArray(new Recipient.Bcc[0]);
    }

    public static Recipient.Bcc[] bcc(InternetAddress... addresses) {
        return Arrays.stream(addresses)
            .map(Recipient::bcc)
            .toArray(Recipient.Bcc[]::new);
    }


    public static class To extends Recipient {

        private To(InternetAddress address) {
            super(address, RecipientType.TO);
        }
    }

    public static class Cc extends Recipient {

        private Cc(InternetAddress address) {
            super(address, RecipientType.CC);
        }
    }

    public static class Bcc extends Recipient {

        private Bcc(InternetAddress address) {
            super(address, RecipientType.BCC);
        }
    }
}
