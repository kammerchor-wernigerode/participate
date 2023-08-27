package de.vinado.wicket.participate.features;

import de.vinado.wicket.participate.model.Event;
import de.vinado.wicket.participate.model.InvitationStatus;
import de.vinado.wicket.participate.model.MockedEvent;
import de.vinado.wicket.participate.model.Participant;
import de.vinado.wicket.participate.services.EventService;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static de.vinado.wicket.participate.model.InvitationStatus.*;
import static org.mockito.Mockito.*;

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

    @ParameterizedTest
    @MethodSource("nonEligibleStatuses")
    void runningJobWithinIntervalButNoEligibleParticipants_shouldNotInvite(InvitationStatus status) {
        Event event = MockedEvent.mockEvent(1L, DateUtils.addDays(NOW, 3));
        Participant participant = mock(Participant.class);
        List<Participant> participants = Collections.singletonList(participant);

        when(participant.getInvitationStatus()).thenReturn(status);
        when(eventService.getUpcomingEvents()).thenReturn(List.of(event));
        when(eventService.getInvitedParticipants(event)).thenReturn(participants);

        feature.run();

        verify(eventService, never()).inviteParticipants(participants, null);
    }

    @ParameterizedTest
    @MethodSource("nonEligibleStatuses")
    void runningJobOutsideIntervalButNoEligibleParticipants_shouldNotInvite(InvitationStatus status) {
        Event event = MockedEvent.mockEvent(1L, DateUtils.addDays(NOW, 30));
        Participant participant = mock(Participant.class);
        List<Participant> participants = Collections.singletonList(participant);

        when(participant.getInvitationStatus()).thenReturn(status);
        when(eventService.getUpcomingEvents()).thenReturn(List.of(event));
        when(eventService.getInvitedParticipants(event)).thenReturn(participants);

        feature.run();

        verify(eventService, never()).inviteParticipants(participants, null);
    }

    @ParameterizedTest
    @MethodSource("eligibleStatuses")
    void runningJobWithinIntervalAndEligibleStatus_shouldInvite(InvitationStatus status) {
        Event event = MockedEvent.mockEvent(2L, DateUtils.addDays(NOW, 14));
        Participant participant = mock(Participant.class);
        List<Participant> participants = Collections.singletonList(participant);

        when(participant.getInvitationStatus()).thenReturn(status);
        when(eventService.getUpcomingEvents()).thenReturn(List.of(event));
        when(eventService.getInvitedParticipants(event)).thenReturn(participants);

        feature.run();

        verify(eventService, times(1)).inviteParticipants(participants, null);
    }

    @ParameterizedTest
    @MethodSource("eligibleStatuses")
    void runningJobOutsideIntervalAndEligibleStatus_shouldNotInvite(InvitationStatus status) {
        Event event = MockedEvent.mockEvent(3L, DateUtils.addDays(NOW, 30));
        Participant participant = mock(Participant.class);
        List<Participant> participants = Collections.singletonList(participant);

        when(participant.getInvitationStatus()).thenReturn(status);
        when(eventService.getUpcomingEvents()).thenReturn(List.of(event));
        when(eventService.getInvitedParticipants(event)).thenReturn(participants);

        feature.run();

        verify(eventService, never()).inviteParticipants(participants, null);
    }

    private static Arguments[] nonEligibleStatuses() {
        return new Arguments[]{
            Arguments.of(ACCEPTED),
            Arguments.of(DECLINED),
            Arguments.of(UNINVITED),
        };
    }

    private static Arguments[] eligibleStatuses() {
        return new Arguments[]{
            Arguments.of(PENDING),
            Arguments.of(TENTATIVE),
        };
    }
}
