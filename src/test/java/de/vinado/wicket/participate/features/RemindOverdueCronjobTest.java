package de.vinado.wicket.participate.features;

import de.vinado.wicket.participate.model.Event;
import de.vinado.wicket.participate.model.MockedEvent;
import de.vinado.wicket.participate.model.Participant;
import de.vinado.wicket.participate.services.EventService;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static de.vinado.wicket.participate.model.InvitationStatus.ACCEPTED;
import static de.vinado.wicket.participate.model.InvitationStatus.PENDING;
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
    void runningJobWithinIntervalButNoEligibleParticipants_shouldNotInvite() {
        Event event = MockedEvent.mockEvent(1L, DateUtils.addDays(NOW, 3));
        Participant participant = mock(Participant.class);
        List<Participant> participants = Collections.singletonList(participant);

        when(participant.getInvitationStatus()).thenReturn(ACCEPTED);
        when(eventService.getUpcomingEvents()).thenReturn(List.of(event));
        when(eventService.getInvitedParticipants(event)).thenReturn(participants);

        feature.run();

        verify(eventService, never()).inviteParticipants(participants, null);
    }

    @Test
    void runningJobWithinIntervalAndPendingInvitation_shouldInvite() {
        Event event = MockedEvent.mockEvent(2L, DateUtils.addDays(NOW, 14));
        Participant participant = mock(Participant.class);
        List<Participant> participants = Collections.singletonList(participant);

        when(participant.getInvitationStatus()).thenReturn(PENDING);
        when(eventService.getUpcomingEvents()).thenReturn(List.of(event));
        when(eventService.getInvitedParticipants(event)).thenReturn(participants);

        feature.run();

        verify(eventService, times(1)).inviteParticipants(participants, null);
    }

    @Test
    void runningJobOutsideIntervalAndPendingInvitation_shouldNotInvite() {
        Event event = MockedEvent.mockEvent(3L, DateUtils.addDays(NOW, 30));
        Participant participant = mock(Participant.class);
        List<Participant> participants = Collections.singletonList(participant);

        when(participant.getInvitationStatus()).thenReturn(PENDING);
        when(eventService.getUpcomingEvents()).thenReturn(List.of(event));
        when(eventService.getInvitedParticipants(event)).thenReturn(participants);

        feature.run();

        verify(eventService, never()).inviteParticipants(participants, null);
    }
}
