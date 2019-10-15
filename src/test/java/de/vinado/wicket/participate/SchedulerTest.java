package de.vinado.wicket.participate;

import de.vinado.wicket.participate.configuration.ApplicationProperties;
import de.vinado.wicket.participate.configuration.Feature;
import de.vinado.wicket.participate.email.service.EmailService;
import de.vinado.wicket.participate.model.MockedEvent;
import de.vinado.wicket.participate.model.Participant;
import de.vinado.wicket.participate.services.EventService;
import de.vinado.wicket.participate.services.PersonService;
import lombok.var;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Vincent Nadoll
 */
public class SchedulerTest {

    private static final Date NOW = new Date();

    private ApplicationProperties properties;
    private EventService eventService;
    private PersonService personService;
    private EmailService emailService;
    private Scheduler scheduler;

    @Before
    public void setUp() throws Exception {
        properties = spy(ApplicationProperties.class);
        eventService = mock(EventService.class);
        personService = mock(PersonService.class);
        emailService = mock(EmailService.class);

        scheduler = new Scheduler(properties, eventService, personService, emailService);

        var features = spy(new ApplicationProperties.Features());
        doAnswer(a -> true).when(features).hasFeature(Feature.NOTIFY_SCORES_MANAGER);
        doAnswer(a -> true).when(features).hasFeature(Feature.REMIND_OVERDUE);
        doAnswer(a -> "scores-manager@kch.participate").when(features).getScoresManagerEmail();
        doAnswer(a -> features).when(properties).getFeatures();

        var mail = spy(new ApplicationProperties.Mail());
        doAnswer(a -> "test@kch.participate").when(mail).getSender();
        doAnswer(a -> mail).when(properties).getMail();
    }

    @Test
    public void runRemindOverdueJob() {
        var event1 = MockedEvent.mockEvent(1L);
        var event2 = MockedEvent.mockEvent(2L, DateUtils.addDays(NOW, 14));

        var participant11 = mock(Participant.class);
        when(participant11.isPending()).thenReturn(false);
        var event1Participants = Collections.singletonList(participant11);

        var participant21 = mock(Participant.class);
        when(participant21.isPending()).thenReturn(true);
        var event2Participants = Collections.singletonList(participant21);


        when(eventService.getUpcomingEvents()).thenReturn(Arrays.asList(event1, event2));
        when(eventService.getInvitedParticipants(event1)).thenReturn(event1Participants);
        when(eventService.getInvitedParticipants(event2)).thenReturn(event2Participants);

        scheduler.runRemindOverdueJob();

        verify(eventService, never()).inviteParticipants(event1Participants, null);
        verify(eventService, times(1)).inviteParticipants(event2Participants, null);
    }
}
