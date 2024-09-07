package de.vinado.app.participate.event.model;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Comparator;

@Value
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Interval implements Serializable, Comparable<Interval> {

    @NonNull
    LocalDate start;

    @NonNull
    LocalDate end;

    @Override
    public int compareTo(Interval that) {
        Comparator<Interval> comparator = Comparator.comparing(Interval::getStart).thenComparing(Interval::getEnd);
        return Comparator.nullsFirst(comparator)
                .compare(this, that);
    }

    public static Builder from(@NonNull LocalDate start) {
        return new Builder(start);
    }


    @RequiredArgsConstructor
    public static class Builder {

        private final LocalDate start;

        public Interval to(@NonNull LocalDate end) {
            assertNotNegative(start, end);
            return new Interval(start, end);
        }

        private static void assertNotNegative(LocalDate start, LocalDate end) {
            if (start.isAfter(end)) {
                throw new NegativeRangeException();
            }
        }
    }

    public static class NegativeRangeException extends RuntimeException {
    }
}
