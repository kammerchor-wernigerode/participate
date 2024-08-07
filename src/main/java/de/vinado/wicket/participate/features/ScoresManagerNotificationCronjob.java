package de.vinado.wicket.participate.features;

import com.opencsv.CSVWriter;
import de.vinado.wicket.participate.email.Email;
import de.vinado.wicket.participate.email.EmailAttachment;
import de.vinado.wicket.participate.email.EmailBuilderFactory;
import de.vinado.wicket.participate.email.service.EmailService;
import de.vinado.wicket.participate.model.Event;
import de.vinado.wicket.participate.model.Participant;
import de.vinado.wicket.participate.model.Person;
import de.vinado.wicket.participate.model.Singer;
import de.vinado.wicket.participate.services.EventService;
import de.vinado.wicket.participate.services.PersonService;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.wicket.util.string.Strings;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.pivovarit.function.ThrowingFunction.sneaky;
import static de.vinado.wicket.participate.common.DateUtils.toLocalDate;
import static de.vinado.wicket.participate.features.ScoresManagerNotificationCronjob.Configuration.CRON_EXPRESSION;
import static de.vinado.wicket.participate.features.ScoresManagerNotificationCronjob.Configuration.FEATURE_ENABLED;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.temporal.ChronoUnit.DAYS;

@Slf4j
@Component
@ConditionalOnExpression(FEATURE_ENABLED)
@EnableConfigurationProperties(ScoresManagerNotificationCronjob.Configuration.class)
@RequiredArgsConstructor
public class ScoresManagerNotificationCronjob {

    private final @NonNull EmailBuilderFactory emailBuilderFactory;
    private final @NonNull Configuration configuration;
    private final @NonNull EventService eventService;
    private final @NonNull PersonService personService;
    private final @NonNull EmailService emailService;

    @Scheduled(cron = CRON_EXPRESSION)
    public void run() {
        try {
            log.info("Enter score's manager reminder job");

            String scoresManagerEmail = configuration.getScoresManagerEmail();
            requireNonEmpty(scoresManagerEmail, "Score's manager email must not be null when NOTIFY_SCORES_MANAGER is enabled");

            Optional<Person> scoresManager = Optional.ofNullable(personService.getPerson(scoresManagerEmail));
            if (!scoresManager.isPresent()) {
                log.info("Score's manager's name could not be determined");
            }

            InternetAddress recipient = scoresManager
                .map(Person::getDisplayName)
                .map(sneaky(name -> new InternetAddress(scoresManagerEmail, name, UTF_8.name())))
                .orElse(new InternetAddress(scoresManagerEmail));

            Stream<Email> emails = eventService.getUpcomingEvents()
                .stream()
                .filter(this::filter)
                .map(event -> prepare(event, recipient))
                .filter(Objects::nonNull);

            emailService.send(emails.collect(Collectors.toList()), "scoresManagerNotification-txt.ftl", null);
            log.info("Ran score's manager reminder job");
        } catch (AddressException e) {
            log.error("Malformed email address encountered", e);
        }
    }

    private boolean filter(Event event) {
        LocalDate now = LocalDate.now();
        LocalDate startDate = toLocalDate(event.getStartDate());
        boolean future = LocalDate.now().isBefore(startDate);
        boolean inScope = configuration.getOffset() >= DAYS.between(now, startDate);
        return future && inScope;
    }

    private Email prepare(Event event, InternetAddress recipient) {
        try {
            List<Singer> attendees = eventService.getInvitedParticipants(event)
                .stream()
                .filter(Participant::isAccepted)
                .map(Participant::getSinger)
                .sorted(Comparator.comparing(Person::getLastName))
                .collect(Collectors.toList());

            return emailBuilderFactory.create()
                .to(recipient)
                .subject("Participant list for event: " + event.getName())
                .message("Attached you will find the list of participants.\n")
                .attachments(new EmailAttachment("attendee-list.csv", MimeType.valueOf("text/csv"), getAttendeeByteArray(attendees)))
                .build()
                ;
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
     * @throws IOException if an error occurs during stream processing
     */
    protected byte[] getAttendeeByteArray(List<Singer> singers) throws IOException {
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            write(singers, stream);
            return stream.toByteArray();
        }
    }

    private void write(List<Singer> singers, ByteArrayOutputStream target) throws IOException {
        try (OutputStreamWriter subject = new OutputStreamWriter(target, UTF_8)) {
            try (CSVWriter writer = new CSVWriter(subject, ';', '\u0000', '\u0000', "\n")) {
                singers.stream()
                    .map(singer -> singer.getLastName() + ", " + singer.getFirstName())
                    .map(name -> new String[]{name})
                    .forEach(writer::writeNext);

                subject.flush();
            }
        }
    }

    private static String requireNonEmpty(String string, String message) throws IllegalArgumentException {
        if (Strings.isEmpty(string)) {
            throw new IllegalArgumentException(message);
        }

        return string;
    }

    @Getter
    @Setter
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
