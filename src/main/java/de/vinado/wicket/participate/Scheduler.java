package de.vinado.wicket.participate;

import com.opencsv.CSVWriter;
import de.vinado.wicket.participate.configuration.ApplicationProperties;
import de.vinado.wicket.participate.email.Email;
import de.vinado.wicket.participate.email.EmailAttachment;
import de.vinado.wicket.participate.email.service.EmailService;
import de.vinado.wicket.participate.model.Participant;
import de.vinado.wicket.participate.model.Person;
import de.vinado.wicket.participate.model.Singer;
import de.vinado.wicket.participate.services.EventService;
import de.vinado.wicket.participate.services.PersonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.wicket.util.string.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeType;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.vinado.wicket.participate.configuration.Feature.NOTIFY_SCORES_MANAGER;
import static de.vinado.wicket.participate.configuration.Feature.REMIND_OVERDUE;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Collections.singleton;

/**
 * Contains scheduled procedures.
 *
 * @author Vincent Nadoll
 */
@Slf4j
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class Scheduler {

    private final ApplicationProperties applicationProperties;
    private final EventService eventService;
    private final PersonService personService;
    private final EmailService emailService;

    /**
     * Searches for every upcoming event in range of +9 days and their accepted invitations to send the singers name and
     * voice to the club's score's manager.
     */
    @Scheduled(cron = "${app.cronjob.notify-scores-manager:0 0 9 ? * FRI}")
    public void runNotifyScoresManagerJob() {
        final ApplicationProperties.Features features = applicationProperties.getFeatures();
        if (!features.hasFeature(NOTIFY_SCORES_MANAGER)) {
            return;
        }
        log.info("Enter score's manager reminder job");

        final String scoresManagerEmail = features.getScoresManagerEmail();
        requireNonEmpty(scoresManagerEmail, "Score's manager email must not be null when NOTIFY_SCORES_MANAGER is enabled");

        final Person scoresManager = personService.getPerson(scoresManagerEmail);
        if (null == scoresManager) {
            log.warn("Score's manager could not be found");
        }

        final Date nextWeek = DateUtils.addDays(new Date(), 9);
        final String from = applicationProperties.getMail().getSender();

        try {
            final InternetAddress recipient = null == scoresManager
                ? new InternetAddress(scoresManagerEmail)
                : new InternetAddress(scoresManagerEmail, scoresManager.getDisplayName());

            final Stream<Email> emails = eventService.getUpcomingEvents()
                .stream()
                .filter(event -> nextWeek.after(event.getStartDate()))
                .map(event -> {
                    try {
                        final Set<Singer> attendees = eventService.getInvitedParticipants(event)
                            .stream()
                            .filter(Participant::isAccepted)
                            .map(Participant::getSinger)
                            .collect(Collectors.toSet());

                        final Email email = new Email();
                        email.setFrom(from);
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
                })
                .filter(Objects::nonNull);

            emailService.send(emails, "scoresManagerNotification-txt.ftl", null);
            log.info("Ran score's manager reminder job");
        } catch (AddressException e) {
            log.error("Malformed email address encountered", e);
        } catch (UnsupportedEncodingException e) {
            log.error("Encoding is unsupported", e);
        }
    }

    /**
     * Searches for every upcoming event in range of +14 days and their pending invitations. Overdue participants
     * receive an email notification.
     */
    @Scheduled(cron = "${app.cronjob.remind-overdue:0 0 9 ? * SUN}")
    public void runRemindOverdueJob() {
        if (!applicationProperties.getFeatures().hasFeature(REMIND_OVERDUE)) {
            return;
        }

        log.info("Enter overdue invitation job");
        final Date nextWeek = DateUtils.addDays(new Date(), 14);
        final Set<Long> eventIds = new HashSet<>();
        final List<Participant> participants = eventService.getUpcomingEvents()
            .stream()
            .filter(event -> nextWeek.after(event.getStartDate()))
            .peek(event -> eventIds.add(event.getId()))
            .map(eventService::getInvitedParticipants)
            .flatMap(List::stream)
            .filter(Participant::isPending)
            .collect(Collectors.toList());

        final int invitationsSent = eventService.inviteParticipants(participants, null);

        final int eventAmount = eventIds.size();
        log.info("Ran overdue job for {} event{} /w ids=[{}]", eventAmount, 1 == eventAmount ? "" : "s", StringUtils.join(eventIds, ", "));
        log.info("Sent {} invitation{}.", invitationsSent, 1 == invitationsSent ? "" : "s");
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
    private byte[] getAttendeeByteArray(final Collection<Singer> singers) throws IOException {
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
     * Checks that the string is empty and throws a customized {@link NullPointerException} if it is.
     *
     * @param string  the string to check for emptiness
     * @param message detail message to be used in the event that a {@code NullPointerException} is thrown
     * @return {@code string} if not empty
     *
     * @throws NullPointerException if {@code string} is empty
     */
    private static String requireNonEmpty(final String string, final String message) {
        if (Strings.isEmpty(string)) {
            throw new NullPointerException(message);
        }

        return string;
    }
}
