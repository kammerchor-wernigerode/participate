package de.vinado.wicket.participate.email;

import de.vinado.wicket.participate.model.Person;

import javax.mail.internet.InternetAddress;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

/**
 * @author Vincent Nadoll
 */
public final class InternetAddressFactory {

    private static final String UTF_8 = StandardCharsets.UTF_8.name();

    public static InternetAddress create(Person person) {
        try {
            return new InternetAddress(person.getEmail(), person.getDisplayName(), UTF_8);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static InternetAddress create(String email, String name) {
        try {
            return new InternetAddress(email, name, UTF_8);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static InternetAddress create(String email) {
        try {
            return new InternetAddress(email, UTF_8);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
