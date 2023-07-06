package de.vinado.wicket.participate.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ThreadLocalRandom;

import static de.vinado.wicket.participate.model.Accommodation.Status.SEARCHING;
import static java.lang.Math.abs;
import static org.junit.jupiter.api.Assertions.*;

class AccommodationTests {

    private static final Accommodation.Status STATUS = SEARCHING;
    private static final Integer BEDS = abs(randomInteger());

    private Accommodation accommodation;

    @BeforeEach
    void setUp() {
        accommodation = new Accommodation(STATUS, BEDS);
    }

    @Test
    void givenNullStatus_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new Accommodation(null, BEDS));
    }

    @Test
    void givenNullBeds_shouldNotThrowException() {
        assertDoesNotThrow(() -> new Accommodation(STATUS, null));
    }

    @Test
    void givenNegativeBedCount_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new Accommodation(STATUS, -abs(randomInteger() - 1)));
    }

    @Test
    void givenZeroBedCount_shouldNotFail() {
        assertDoesNotThrow(() -> new Accommodation(STATUS, 0));
    }

    @Test
    void givenPositiveBedCount_shouldNotFail() {
        assertDoesNotThrow(() -> new Accommodation(STATUS, abs(randomInteger()) + 1));
    }

    @Test
    void accommodation_shouldProvideGivenStatus() {
        Accommodation.Status status = accommodation.getStatus();

        assertEquals(STATUS, status);
    }

    @Test
    void accommodation_shouldProvideGivenBeds() {
        Integer beds = accommodation.getBeds();

        assertEquals(BEDS, beds);
    }

    private static Integer randomInteger() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return random.nextInt();
    }
}
