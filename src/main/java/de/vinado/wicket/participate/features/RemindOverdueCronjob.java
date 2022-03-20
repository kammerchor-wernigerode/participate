package de.vinado.wicket.participate.features;

import de.vinado.wicket.participate.model.Event;
import de.vinado.wicket.participate.model.Participant;
import de.vinado.wicket.participate.services.EventService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Min;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static de.vinado.wicket.participate.common.DateUtils.toLocalDate;
import static de.vinado.wicket.participate.features.RemindOverdueCronjob.Configuration.CRON_EXPRESSION;
import static de.vinado.wicket.participate.features.RemindOverdueCronjob.Configuration.FEATURE_ENABLED;
import static java.time.temporal.ChronoUnit.DAYS;

/**
 * Sets up a Cronjob which reminds participants about upcoming events.
 *
 * @author Vincent Nadoll
 */
@Slf4j
@Component
@ConditionalOnExpression(FEATURE_ENABLED)
@RequiredArgsConstructor
public class RemindOverdueCronjob {

    private final Configuration configuration;
    private final EventService eventService;

    /**
     * Searches for upcoming events and their pending invitations. Overdue participants receive an email notification.
     */
    @Scheduled(cron = CRON_EXPRESSION)
    public void run() {
        log.info("Enter overdue invitation job");

        final Set<Long> eventIds = new HashSet<>();
        final List<Participant> participants = eventService.getUpcomingEvents()
            .stream()
            .filter(this::filter)
            .peek(event -> eventIds.add(event.getId()))
            .map(eventService::getInvitedParticipants)
            .flatMap(List::stream)
            .filter(Participant::isPending)
            .collect(Collectors.toList());

        eventService.inviteParticipants(participants, null);

        final int eventAmount = eventIds.size();
        log.info("Ran overdue job for {} event{} /w ids=[{}]", eventAmount, 1 == eventAmount ? "" : "s", StringUtils.join(eventIds, ", "));
    }

    /**
     * @param event the event to filter
     * @return {@code true} if the event's start date is between now and the configured day; {@code false} otherwise
     */
    private boolean filter(Event event) {
        LocalDate now = LocalDate.now();
        LocalDate startDate = toLocalDate(event.getStartDate());
        boolean future = LocalDate.now().isBefore(startDate);
        boolean inScope = configuration.getOffset() >= DAYS.between(now, startDate);
        return future && inScope;
    }

    /**
     * Configuration class for the {@link RemindOverdueCronjob} feature.
     */
    @Getter
    @Setter
    @org.springframework.context.annotation.Configuration
    @ConfigurationProperties("app.features.remind-overdue")
    @ConditionalOnExpression(FEATURE_ENABLED)
    static class Configuration {

        public static final String FEATURE_ENABLED = "'${app.features.remind-overdue.enabled:false}' == 'true'";
        public static final String CRON_EXPRESSION = "${app.features.remind-overdue.cron-expression:0 0 9 ? * SUN}";

        private boolean enabled = false;
        private String cronExpression = "0 0 9 ? * SUN";
        private @Min(1L) int offset = 14;
    }
}
