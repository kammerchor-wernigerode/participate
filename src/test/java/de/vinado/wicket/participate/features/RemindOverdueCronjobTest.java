package de.vinado.wicket.participate.features;

import de.vinado.wicket.participate.model.MockedEvent;
import de.vinado.wicket.participate.model.Participant;
import de.vinado.wicket.participate.services.EventService;
import lombok.var;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Vincent Nadoll
 */
public class RemindOverdueCronjobTest {

    private static final Date NOW = new Date();

    private EventService eventService;
    private RemindOverdueCronjob feature;

    @Before
    public void setUp() throws Exception {
        eventService = mock(EventService.class);
        feature = new RemindOverdueCronjob(new RemindOverdueCronjob.Configuration(), eventService);
    }

    @Test
    public void run() {
        var event1 = MockedEvent.mockEvent(1L);
        var event2 = MockedEvent.mockEvent(2L, DateUtils.addDays(NOW, 14));
        var event3 = MockedEvent.mockEvent(3L, DateUtils.addDays(NOW, 30));

        var participant11 = mock(Participant.class);
        when(participant11.isPending()).thenReturn(false);
        var event1Participants = Collections.singletonList(participant11);

        var participant21 = mock(Participant.class);
        when(participant21.isPending()).thenReturn(true);
        var event2Participants = Collections.singletonList(participant21);

        var participant31 = mock(Participant.class);
        when(participant31.isPending()).thenReturn(true);
        var event3Participants = Collections.singletonList(participant31);


        when(eventService.getUpcomingEvents()).thenReturn(Arrays.asList(event1, event2, event3));
        when(eventService.getInvitedParticipants(event1)).thenReturn(event1Participants);
        when(eventService.getInvitedParticipants(event2)).thenReturn(event2Participants);
        when(eventService.getInvitedParticipants(event3)).thenReturn(event3Participants);

        feature.run();

        verify(eventService, never()).inviteParticipants(event1Participants, null);
        verify(eventService, times(1)).inviteParticipants(event2Participants, null);
        verify(eventService, never()).inviteParticipants(event3Participants, null);
    }
}
