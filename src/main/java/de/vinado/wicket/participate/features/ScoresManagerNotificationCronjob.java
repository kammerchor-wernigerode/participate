package de.vinado.wicket.participate.features;

import com.opencsv.CSVWriter;
import de.vinado.app.participate.event.model.EventName;
import de.vinado.app.participate.notification.email.app.EmailService;
import de.vinado.app.participate.notification.email.model.Email;
import de.vinado.app.participate.notification.email.model.EmailException;
import de.vinado.app.participate.notification.email.model.Recipient;
import de.vinado.app.participate.notification.email.model.TemplatedEmailFactory;
import de.vinado.app.participate.notification.email.support.InMemoryAttachment;
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
import org.springframework.format.Printer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeType;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.pivovarit.function.ThrowingFunction.sneaky;
import static de.vinado.app.participate.notification.email.app.SendEmail.send;
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

    private final @NonNull TemplatedEmailFactory emailFactory;
    private final @NonNull Configuration configuration;
    private final @NonNull EventService eventService;
    private final @NonNull PersonService personService;
    private final @NonNull EmailService emailService;
    private final @NonNull Printer<EventName> eventNamePrinter;

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

            Email[] emails = eventService.getUpcomingEvents()
                .stream()
                .filter(this::filter)
                .map(this::prepare)
                .filter(Objects::nonNull)
                .toArray(Email[]::new);

            for (Email email : emails) {
                emailService.execute(send(email).atOnce(Recipient.to(recipient)));
            }
            log.info("Ran score's manager reminder job");
        } catch (AddressException e) {
            log.error("Malformed email address encountered", e);
        } catch (EmailException e) {
            log.error("Unable to send email", e);
        }
    }

    private boolean filter(Event event) {
        LocalDate now = LocalDate.now();
        LocalDate startDate = toLocalDate(event.getStartDate());
        boolean future = LocalDate.now().isBefore(startDate);
        boolean inScope = configuration.getOffset() >= DAYS.between(now, startDate);
        return future && inScope;
    }

    private Email prepare(Event event) {
        try {
            List<Singer> attendees = eventService.getInvitedParticipants(event)
                .stream()
                .filter(Participant::isAccepted)
                .map(Participant::getSinger)
                .sorted(Comparator.comparing(Person::getLastName))
                .collect(Collectors.toList());

            String subject = "Teilnehmerliste für " + eventNamePrinter.print(EventName.of(event), Locale.getDefault());
            String plaintextTemplatePath = "scoresManagerNotification-txt.ftl";
            Map<String, Object> data = Collections.emptyMap();
            Set<Email.Attachment> attachments = attachments(attendees);
            return emailFactory.create(subject, plaintextTemplatePath, null, data, attachments);
        } catch (IOException e) {
            log.error("Unable to write stream to CSV attachment", e);
        }

        return null;
    }

    private Set<Email.Attachment> attachments(List<Singer> attendees) throws IOException {
        String name = "teilnehmerliste.csv";
        MimeType type = MimeType.valueOf("text/csv");
        byte[] data = getAttendeeByteArray(attendees);
        Email.Attachment attachment = new InMemoryAttachment(name, type, data);
        return Collections.singleton(attachment);
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
