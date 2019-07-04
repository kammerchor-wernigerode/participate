package de.vinado.wicket.participate.tasks;

import de.vinado.wicket.participate.configuration.ApplicationProperties;
import de.vinado.wicket.participate.services.EventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.stream.Collectors;

import static de.vinado.wicket.participate.configuration.Feature.REMIND_OVERDUE;
import static de.vinado.wicket.participate.model.InvitationStatus.PENDING;
import static java.util.Calendar.DATE;

/**
 * @author Vincent Nadoll
 */
@Component
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class InvitationTask {

    private final ApplicationProperties applicationProperties;
    private final EventService eventService;

    private static Date addDays(final Date date, final int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(DATE, days);
        return cal.getTime();
    }

    @Scheduled(cron = "0 0 9 ? * SUN")
    public void remindOverdue() {
        if (applicationProperties.getFeatures().hasFeature(REMIND_OVERDUE)) {
            final Date nextWeek = addDays(new Date(), 7);
            log.info("Run overdue invitations job");

            eventService.getUpcomingEvents().stream()
                .filter(event -> nextWeek.after(event.getStartDate()))
                .forEach(event -> {
                    final int invitations = eventService.inviteParticipants(eventService.getParticipants(event, true).stream()
                        .filter(participant -> PENDING.equals(participant.getInvitationStatus()))
                        .collect(Collectors.toList()));

                    log.info("Ran overdue job for event w\\ id=" + event.getId() + " and sent email to " + invitations + " participants.");
                });
        }
    }
}
