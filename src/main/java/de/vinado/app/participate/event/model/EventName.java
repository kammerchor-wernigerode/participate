package de.vinado.app.participate.event.model;

import lombok.NonNull;

import java.io.Serializable;
import java.util.Comparator;

public record EventName(@NonNull String value, @NonNull Interval interval)
        implements Serializable, Comparable<EventName> {

    @Override
    public int compareTo(EventName that) {
        Comparator<EventName> comparator = Comparator.comparing(EventName::interval);
        return Comparator.nullsFirst(comparator)
                .compare(this, that);
    }
}
