package de.vinado.wicket.participate.model;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ThreadLocalRandom;

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
        Accommodation accommodation = mock(Accommodation.class);
        event.setParticipants(singletonList(participant));
        when(accommodation.isSearching()).thenReturn(false);
        when(participant.isConsiderable()).thenReturn(true);
        when(participant.accommodation()).thenReturn(accommodation);

        int result = event.getAccommodationCount();

        assertEquals(0, result);
    }

    @Test
    void singleSearchingParticipant_shouldHaveOneAccommodation() {
        Participant participant = mock(Participant.class);
        EventDetails event = new EventDetails();
        Accommodation accommodation = mock(Accommodation.class);
        event.setParticipants(singletonList(participant));
        when(accommodation.isSearching()).thenReturn(true);
        when(accommodation.getBeds()).thenReturn(1);
        when(participant.accommodation()).thenReturn(accommodation);
        when(participant.isConsiderable()).thenReturn(true);

        int result = event.getAccommodationCount();

        assertEquals(1, result);
    }

    @Test
    void singleSearchingParticipant_shouldSumBeds() {
        int beds = Arbitrary.positiveInt();
        Participant participant = mock(Participant.class);
        EventDetails event = new EventDetails();
        Accommodation accommodation = mock(Accommodation.class);
        event.setParticipants(singletonList(participant));
        when(accommodation.isSearching()).thenReturn(true);
        when(accommodation.getBeds()).thenReturn(beds);
        when(participant.accommodation()).thenReturn(accommodation);
        when(participant.isConsiderable()).thenReturn(true);

        int result = event.getAccommodationCount();

        assertEquals(beds, result);
    }

    @Test
    void twoSearchingParticipants_shouldHaveTwoAccommodation() {
        Participant participant1 = mock(Participant.class);
        Accommodation accommodation1 = mock(Accommodation.class);
        Participant participant2 = mock(Participant.class);
        Accommodation accommodation2 = mock(Accommodation.class);
        EventDetails event = new EventDetails();
        event.setParticipants(asList(participant1, participant2));
        when(accommodation1.isSearching()).thenReturn(true);
        when(accommodation1.getBeds()).thenReturn(1);
        when(participant1.accommodation()).thenReturn(accommodation1);
        when(participant1.isConsiderable()).thenReturn(true);
        when(accommodation2.isSearching()).thenReturn(true);
        when(accommodation2.getBeds()).thenReturn(1);
        when(participant2.accommodation()).thenReturn(accommodation2);
        when(participant2.isConsiderable()).thenReturn(true);

        int result = event.getAccommodationCount();

        assertEquals(2, result);
    }


    private static class Arbitrary {

        private static int positiveInt() {
            ThreadLocalRandom random = ThreadLocalRandom.current();
            return random.nextInt(9999) + 1;
        }
    }
}