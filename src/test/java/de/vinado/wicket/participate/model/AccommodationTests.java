package de.vinado.wicket.participate.model;

import de.vinado.wicket.participate.model.Accommodation.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static de.vinado.wicket.participate.model.Accommodation.Status.SEARCHING;
import static java.lang.Math.abs;
import static java.util.function.Predicate.not;
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

    @ParameterizedTest
    @MethodSource("searching")
    void accommodation_shouldProvideSearching(Status status) {
        accommodation = new Accommodation(status, BEDS);

        boolean result = accommodation.isSearching();

        assertTrue(result);
    }

    private static Stream<Arguments> searching() {
        return Arrays.stream(Status.values())
            .filter(searchingPredicate())
            .map(Arguments::of);
    }

    @ParameterizedTest
    @MethodSource("notSearching")
    void accommodation_shouldNotProvideSearching(Status status) {
        accommodation = new Accommodation(status, BEDS);

        boolean result = accommodation.isSearching();

        assertFalse(result);
    }

    private static Stream<Arguments> notSearching() {
        return Arrays.stream(Status.values())
            .filter(not(searchingPredicate()))
            .map(Arguments::of);
    }

    private static Predicate<Status> searchingPredicate() {
        return SEARCHING::equals;
    }

    private static Integer randomInteger() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return random.nextInt();
    }
}
