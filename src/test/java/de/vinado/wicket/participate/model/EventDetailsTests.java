package de.vinado.wicket.participate.model;

import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventDetailsTests {

    @Test
    void emptyParticipants_shouldHaveZeroAccommodations() {
        EventDetails event = new EventDetails();
        event.setParticipants(emptyList());

        int result = event.getAccommodationCount();

        assertEquals(0, result);
    }

    @Test
    void singleIndependentParticipant_shouldHaveZeroAccommodations() {
        Participant participant = mock(Participant.class);
        EventDetails event = new EventDetails();
        event.setParticipants(singletonList(participant));
        when(participant.isAccommodation()).thenReturn(false);

        int result = event.getAccommodationCount();

        assertEquals(0, result);
    }

    @Test
    void singleSearchingParticipant_shouldHaveOneAccommodation() {
        Participant participant = mock(Participant.class);
        EventDetails event = new EventDetails();
        event.setParticipants(singletonList(participant));
        when(participant.isAccommodation()).thenReturn(true);

        int result = event.getAccommodationCount();

        assertEquals(1, result);
    }

    @Test
    void twoSearchingParticipants_shouldHaveTwoAccommodation() {
        Participant participant1 = mock(Participant.class);
        Participant participant2 = mock(Participant.class);
        EventDetails event = new EventDetails();
        event.setParticipants(asList(participant1, participant2));
        when(participant1.isAccommodation()).thenReturn(true);
        when(participant2.isAccommodation()).thenReturn(true);

        int result = event.getAccommodationCount();

        assertEquals(2, result);
    }
}
