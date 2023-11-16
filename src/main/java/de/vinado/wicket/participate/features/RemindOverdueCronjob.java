package de.vinado.wicket.participate.features;

import de.vinado.wicket.participate.model.Event;
import de.vinado.wicket.participate.model.InvitationStatus;
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

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.constraints.Min;

import static de.vinado.wicket.participate.common.DateUtils.toLocalDate;
import static de.vinado.wicket.participate.features.RemindOverdueCronjob.Configuration.CRON_EXPRESSION;
import static de.vinado.wicket.participate.features.RemindOverdueCronjob.Configuration.FEATURE_ENABLED;
import static java.time.temporal.ChronoUnit.DAYS;

@Slf4j
@Component
@ConditionalOnExpression(FEATURE_ENABLED)
@RequiredArgsConstructor
public class RemindOverdueCronjob {

    private final Configuration configuration;
    private final EventService eventService;

    @Scheduled(cron = CRON_EXPRESSION)
    public void run() {
        log.info("Enter overdue invitation job");

        Set<Long> eventIds = new HashSet<>();
        List<Participant> participants = eventService.getUpcomingEvents()
            .stream()
            .filter(this::filter)
            .peek(event -> eventIds.add(event.getId()))
            .map(eventService::getInvitedParticipants)
            .flatMap(List::stream)
            .filter(InvitationStatus.by(InvitationStatus.PENDING).or(InvitationStatus.by(InvitationStatus.TENTATIVE)))
            .collect(Collectors.toList());

        eventService.inviteParticipants(participants, null);

        int eventAmount = eventIds.size();
        log.info("Ran overdue job for {} event{} /w ids=[{}]", eventAmount, 1 == eventAmount ? "" : "s", StringUtils.join(eventIds, ", "));
    }

    private boolean filter(Event event) {
        LocalDate now = LocalDate.now();
        LocalDate startDate = toLocalDate(event.getStartDate());
        boolean future = LocalDate.now().isBefore(startDate);
        boolean inScope = configuration.getOffset() >= DAYS.between(now, startDate);
        return future && inScope;
    }

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
