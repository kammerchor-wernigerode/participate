package de.vinado.wicket.participate.features;

import de.vinado.wicket.participate.model.Event;
import de.vinado.wicket.participate.model.MockedEvent;
import de.vinado.wicket.participate.model.Participant;
import de.vinado.wicket.participate.services.EventService;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Vincent Nadoll
 */
class RemindOverdueCronjobTest {

    private static final Date NOW = new Date();

    private EventService eventService;
    private RemindOverdueCronjob feature;

    @BeforeEach
    void setUp() {
        eventService = mock(EventService.class);
        feature = new RemindOverdueCronjob(new RemindOverdueCronjob.Configuration(), eventService);
    }

    @Test
    void run() {
        Event event1 = MockedEvent.mockEvent(1L);
        Event event2 = MockedEvent.mockEvent(2L, DateUtils.addDays(NOW, 14));
        Event event3 = MockedEvent.mockEvent(3L, DateUtils.addDays(NOW, 30));

        Participant participant11 = mock(Participant.class);
        when(participant11.isPending()).thenReturn(false);
        List<Participant> event1Participants = Collections.singletonList(participant11);

        Participant participant21 = mock(Participant.class);
        when(participant21.isPending()).thenReturn(true);
        List<Participant> event2Participants = Collections.singletonList(participant21);

        Participant participant31 = mock(Participant.class);
        when(participant31.isPending()).thenReturn(true);
        List<Participant> event3Participants = Collections.singletonList(participant31);


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
