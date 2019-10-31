package de.vinado.wicket.participate.features;

import com.opencsv.CSVWriter;
import de.vinado.wicket.participate.configuration.ApplicationProperties;
import de.vinado.wicket.participate.email.Email;
import de.vinado.wicket.participate.email.EmailAttachment;
import de.vinado.wicket.participate.email.service.EmailService;
import de.vinado.wicket.participate.model.Event;
import de.vinado.wicket.participate.model.Participant;
import de.vinado.wicket.participate.model.Person;
import de.vinado.wicket.participate.model.Singer;
import de.vinado.wicket.participate.services.EventService;
import de.vinado.wicket.participate.services.PersonService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.wicket.util.string.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeType;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.pivovarit.function.ThrowingFunction.sneaky;
import static de.vinado.wicket.participate.common.DateUtils.convert;
import static de.vinado.wicket.participate.features.ScoresManagerNotificationCronjob.Configuration.CRON_EXPRESSION;
import static de.vinado.wicket.participate.features.ScoresManagerNotificationCronjob.Configuration.FEATURE_ENABLED;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.Collections.singleton;

/**
 * Sets up a Cronjob which sends an email to the club's score's manager about accepted invitations.
 *
 * @author Vincent Nadoll
 */
@Slf4j
@Component
@ConditionalOnExpression(FEATURE_ENABLED)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ScoresManagerNotificationCronjob {

    private final ApplicationProperties applicationProperties;
    private final Configuration configuration;
    private final EventService eventService;
    private final PersonService personService;
    private final EmailService emailService;

    /**
     * Searches for upcoming events and their accepted invitations to send the singers name to the club's score's
     * manager.
     */
    @Scheduled(cron = CRON_EXPRESSION)
    public void run() {
        try {
            log.info("Enter score's manager reminder job");

            final String scoresManagerEmail = configuration.getScoresManagerEmail();
            requireNonEmpty(scoresManagerEmail, "Score's manager email must not be null when NOTIFY_SCORES_MANAGER is enabled");

            final Optional<Person> scoresManager = Optional.ofNullable(personService.getPerson(scoresManagerEmail));
            if (!scoresManager.isPresent()) {
                log.info("Score's manager's name could not be determined");
            }

            final InternetAddress recipient = scoresManager
                .map(Person::getDisplayName)
                .map(sneaky(name -> new InternetAddress(scoresManagerEmail, name)))
                .orElse(new InternetAddress(scoresManagerEmail));

            final Stream<Email> emails = eventService.getUpcomingEvents()
                .stream()
                .filter(this::filter)
                .map(event -> prepare(event, recipient))
                .filter(Objects::nonNull);

            emailService.send(emails, "scoresManagerNotification-txt.ftl", null);
            log.info("Ran score's manager reminder job");
        } catch (AddressException e) {
            log.error("Malformed email address encountered", e);
        }
    }

    /**
     * @param event the event to filter
     * @return {@code true} if the event's start date is between now and the configured day; {@code false} otherwise
     */
    private boolean filter(Event event) {
        LocalDate now = LocalDate.now();
        Date startDate = event.getStartDate();
        boolean future = new Date().before(startDate);
        boolean inScope = configuration.getOffset() >= DAYS.between(now, convert(startDate));
        return future && inScope;
    }

    /**
     * Maps the given event's participants to an email, with a participation list attached.
     *
     * @param event     the upcoming event
     * @param recipient the scores manager email address
     * @return a prepared email with the participation list attached.
     */
    private Email prepare(Event event, InternetAddress recipient) {
        try {
            final List<Singer> attendees = eventService.getInvitedParticipants(event)
                .stream()
                .filter(Participant::isAccepted)
                .map(Participant::getSinger)
                .sorted(Comparator.comparing(Person::getLastName))
                .collect(Collectors.toList());

            final Email email = new Email();
            email.setFrom(applicationProperties.getMail().getSender());
            email.setTo(singleton(recipient));
            email.setSubject(String.format("Participant list for event: %s", event.getName()));
            email.setMessage("Attached you will find the list of participants.\n");
            email.setAttachments(singleton(
                new EmailAttachment("attendee-list.csv", MimeType.valueOf("text/csv"), getAttendeeByteArray(attendees))
            ));

            return email;
        } catch (AddressException e) {
            log.error("Malformed email address encountered", e);
        } catch (IOException e) {
            log.error("Unable to write stream to CSV attachment", e);
        }

        return null;
    }

    /**
     * Writes the singer's surname and given name to a comma-separated byte array. Even though the CSV holds only one
     * column.
     *
     * @param singers the singers to be written to the byte array
     * @return byte array of CSV data
     *
     * @throws IOException if an error occurs during stream processing
     */
    private byte[] getAttendeeByteArray(final List<Singer> singers) throws IOException {
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            final CSVWriter writer = new CSVWriter(new OutputStreamWriter(stream, UTF_8), ';', '\u0000', '\u0000', "\n");
            singers.stream()
                // this is intended
                .map(singer -> new String[]{String.format("%s, %s", singer.getLastName(), singer.getFirstName())})
                .forEach(writer::writeNext);
            writer.close();

            return stream.toByteArray();
        }
    }

    /**
     * Checks that the string is empty and throws a customized {@link IllegalArgumentException} if it is.
     *
     * @param string  the string to check for emptiness
     * @param message detail message to be used in the event that a {@code IllegalArgumentException} is thrown
     * @return {@code string} if not empty
     *
     * @throws IllegalArgumentException if {@code string} is empty
     */
    private static String requireNonEmpty(final String string, final String message) throws IllegalArgumentException {
        if (Strings.isEmpty(string)) {
            throw new IllegalArgumentException(message);
        }

        return string;
    }

    /**
     * Configuration class for the {@link ScoresManagerNotificationCronjob} feature.
     */
    @Getter
    @Setter
    @org.springframework.context.annotation.Configuration
    @ConfigurationProperties("app.features.scores-manager-notification")
    @ConditionalOnExpression(FEATURE_ENABLED)
    static class Configuration {

        public static final String FEATURE_ENABLED = "'${app.features.scores-manager-notification.enabled:false}' == 'true'";
        public static final String CRON_EXPRESSION = "${app.features.scores-manager-notification.cron-expression:0 0 9 ? * FRI}";

        private boolean enabled = false;
        private String cronExpression = "0 0 9 ? * FRI";
        private @Min(1L) int offset = 9;
        private @Pattern(regexp = "^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,6}$") String scoresManagerEmail;
    }
}
