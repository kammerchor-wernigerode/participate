package de.vinado.wicket.participate.model;

import org.junit.jupiter.api.Test;

import static de.vinado.wicket.participate.model.Accommodation.Status.NO_NEED;
import static de.vinado.wicket.participate.model.Accommodation.Status.OFFERING;
import static de.vinado.wicket.participate.model.Accommodation.Status.SEARCHING;
import static de.vinado.wicket.participate.model.InvitationStatus.UNINVITED;
import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ParticipantTests {

    @Test
    void givenOffering_shouldNotNeedAccommodation() {
        Accommodation accommodation = mock(Accommodation.class);
        Participant participant = createParticipant(accommodation);
        when(accommodation.getStatus()).thenReturn(OFFERING);

        boolean result = participant.isAccommodation();

        assertFalse(result);
    }

    @Test
    void givenNoNeed_shouldNotNeedAccommodation() {
        Accommodation accommodation = mock(Accommodation.class);
        Participant participant = createParticipant(accommodation);
        when(accommodation.getStatus()).thenReturn(NO_NEED);

        boolean result = participant.isAccommodation();

        assertFalse(result);
    }

    @Test
    void givenSearching_shouldNeedAccommodation() {
        Accommodation accommodation = mock(Accommodation.class);
        Participant participant = createParticipant(accommodation);
        when(accommodation.getStatus()).thenReturn(SEARCHING);

        boolean result = participant.isAccommodation();

        assertTrue(result);
    }

    private static Participant createParticipant(Accommodation accommodation) {
        return new Participant(mock(Event.class),
            mock(Singer.class),
            randomString(),
            UNINVITED,
            null,
            null,
            false,
            accommodation,
            (short) -1,
            randomString());
    }

    private static String randomString() {
        return randomUUID().toString();
    }
}
