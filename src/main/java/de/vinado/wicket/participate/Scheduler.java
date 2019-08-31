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
import org.apache.logging.log4j.util.Strings;
import org.apache.wicket.util.io.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeType;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
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
     * A weekly cronjob that runs every sunday at 9 AM.
     */
    @Scheduled(cron = "0 0 9 ? * SUN")
    public void weekly() {
        log.info("Run weekly cronjob: 0 0 9 ? * SUN");

        remindOverdue();
        notifyScoresManager();
    }

    /**
     * Searches for every upcoming event in range of +7 days and their accepted invitations to send the singers name and
     * voice to the club's score's manager.
     */
    private void notifyScoresManager() {
        final ApplicationProperties.Features features = applicationProperties.getFeatures();
        if (!features.hasFeature(NOTIFY_SCORES_MANAGER)) {
            return;
        }
        log.info("Enter score's manager reminder job");

        final String scoresManagerEmail = features.getScoresManagerEmail();
        if (Strings.isBlank(scoresManagerEmail)) {
            throw new NullPointerException("Score's manager email must not be null when NOTIFY_SCORES_MANAGER is enabled");
        }

        final Person scoresManager = personService.getPerson(scoresManagerEmail);
        if (null == scoresManager) {
            log.warn("Score's manager could not be found.");
        }

        final Date nextWeek = DateUtils.addDays(new Date(), 7);
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
                        email.setSubject("Attendee List for " + event.getName());
                        email.setMessage("See attachment for attendee list.\n");
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

            emailService.send(emails);
            log.info("Ran score's manager reminder job");
        } catch (AddressException e) {
            log.error("Malformed email address encountered", e);
        } catch (UnsupportedEncodingException e) {
            log.error("Encoding is unsupported", e);
        }
    }

    /**
     * Searches for every upcoming event in range of +7 days and their pending invitations. Overdue participants receive
     * an email notification.
     */
    private void remindOverdue() {
        if (!applicationProperties.getFeatures().hasFeature(REMIND_OVERDUE)) {
            return;
        }

        log.info("Enter overdue invitation job");
        final Date nextWeek = DateUtils.addDays(new Date(), 7);
        final Set<Long> eventIds = new HashSet<>();
        final List<Participant> participants = eventService.getUpcomingEvents()
            .stream()
            .filter(event -> nextWeek.after(event.getStartDate()))
            .peek(event -> eventIds.add(event.getId()))
            .map(eventService::getInvitedParticipants)
            .flatMap(List::stream)
            .filter(Participant::isPending)
            .collect(Collectors.toList());

        final int invitationsSent = eventService.inviteParticipants(participants);


        final int eventAmount = eventIds.size();
        final StringBuilder infoBuilder = new StringBuilder();
        infoBuilder.append("Ran overdue job for ");
        infoBuilder.append(eventAmount);

        if (eventAmount == 0) {
            infoBuilder.append(" events. ");
        } else if (eventAmount == 1) {
            infoBuilder.append(" event: ");
            infoBuilder.append(StringUtils.join(eventIds, ""));
            infoBuilder.append(" ");
        } else {
            infoBuilder.append(" event:\n\n  ");
            infoBuilder.append(StringUtils.join(eventIds, "\n "));
            infoBuilder.append("\n\n");
        }
        infoBuilder.append("Sent ");
        infoBuilder.append(invitationsSent);
        infoBuilder.append(" invitations.");

        log.info(infoBuilder.toString());
    }

    /**
     * Writes the singer's surname, given name and voice to a CSV byte array.
     *
     * @param singers the singers to write to the CSV byte array
     * @return byte array of singers
     *
     * @throws IOException if an error occurs during closing the streams
     */
    private byte[] getAttendeeByteArray(final Collection<Singer> singers) throws IOException {
        byte[] buffer;

        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            final CSVWriter writer = new CSVWriter(new OutputStreamWriter(stream, UTF_8));
            writer.writeNext(new String[]{"Surname", "Given Name", "Voice"});
            singers.stream()
                .map(singer -> new String[]{singer.getLastName(), singer.getFirstName(), singer.getVoice().name()})
                .forEach(writer::writeNext);
            writer.close();

            buffer = stream.toByteArray();
        }

        return buffer;
    }
}
