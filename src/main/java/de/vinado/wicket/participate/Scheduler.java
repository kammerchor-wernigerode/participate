package de.vinado.wicket.participate;

import de.vinado.wicket.participate.configuration.ApplicationProperties;
import de.vinado.wicket.participate.model.Participant;
import de.vinado.wicket.participate.services.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static de.vinado.wicket.participate.configuration.Feature.REMIND_OVERDUE;
import static java.util.Calendar.DATE;

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

    /**
     * A weekly cronjob that runs every sunday at 9 AM.
     */
    @Scheduled(cron = "0 0 9 ? * SUN")
    public void weekly() {
        log.info("Run weekly cronjob: 0 0 9 ? * SUN");

        remindOverdue();
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
        final Date nextWeek = addDays(new Date(), 7);
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
     * Adds the given days to the given date.
     *
     * @param date date to add days to
     * @param days days to add
     * @return date with added days
     */
    private static Date addDays(final Date date, final int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(DATE, days);
        return cal.getTime();
    }
}
