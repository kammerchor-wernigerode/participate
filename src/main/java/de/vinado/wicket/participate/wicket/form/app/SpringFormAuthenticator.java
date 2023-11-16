package de.vinado.wicket.participate.wicket.form.app;

import de.vinado.wicket.participate.configuration.ApplicationProperties;
import de.vinado.wicket.participate.model.Person;
import de.vinado.wicket.participate.model.User;
import de.vinado.wicket.participate.services.PersonService;
import de.vinado.wicket.participate.services.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class SpringFormAuthenticator implements FormAuthenticator {

    private final ApplicationProperties applicationProperties;
    private final PersonService personService;
    private final UserService userService;

    @Override
    public boolean authenticate(String email, String passwordHash) {
        Authenticator authenticator = new Authenticator(email, passwordHash);

        return retrievePerson(email).map(this::adapt).filter(authenticator)
            .or(() -> retrieveUser(email).map(this::adapt).filter(authenticator))
            .filter(authenticator) // just to be sure
            .isPresent();
    }

    private Optional<Person> retrievePerson(String email) {
        return Optional.ofNullable(personService.getPerson(email));
    }

    private CredentialContainer adapt(Person person) {
        return new PersonCredentials(person, applicationProperties);
    }

    private Optional<User> retrieveUser(String email) {
        return Optional.ofNullable(personService.getPerson(email))
            .map(userService::getUser);
    }

    private CredentialContainer adapt(User user) {
        return new UserCredentials(user);
    }


    private interface CredentialContainer {

        @Nullable
        String getEmail();

        @NonNull
        String getPasswordHash();
    }

    @RequiredArgsConstructor
    private static final class PersonCredentials implements CredentialContainer {

        private final Person person;
        private final ApplicationProperties properties;

        @NonNull
        @Override
        public String getEmail() {
            return person.getEmail();
        }

        @NonNull
        @Override
        public String getPasswordHash() {
            return DigestUtils.sha256Hex(properties.getParticipatePassword());
        }
    }

    @RequiredArgsConstructor
    private static final class UserCredentials implements CredentialContainer {

        private final User user;

        @Nullable
        @Override
        public String getEmail() {
            return Optional.ofNullable(user.getPerson())
                .map(Person::getEmail)
                .orElse(null);
        }

        @NonNull
        @Override
        public String getPasswordHash() {
            return user.getPasswordSha256();
        }
    }


    @RequiredArgsConstructor
    private static final class Authenticator implements Predicate<CredentialContainer> {

        private final String email;
        private final String passwordHash;

        @Override
        public boolean test(CredentialContainer credentials) {
            return matchesEmail(credentials)
                && matchesPassword(credentials);
        }

        private boolean matchesEmail(CredentialContainer credentials) {
            if (anyEmpty(credentials.getEmail(), email)) return false;
            return Objects.equals(email, credentials.getEmail());
        }

        private boolean matchesPassword(CredentialContainer credentials) {
            if (anyEmpty(credentials.getPasswordHash(), passwordHash)) return false;
            return Objects.equals(passwordHash, credentials.getPasswordHash());
        }

        private static boolean anyEmpty(String... strings) {
            return !Arrays.stream(strings).allMatch(StringUtils::hasText);
        }
    }
}
