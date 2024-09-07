package de.vinado.app.participate.event.model;

import de.vinado.wicket.participate.model.Event;
import lombok.NonNull;

import java.io.Serializable;
import java.util.Comparator;

public record EventName(@NonNull String value, @NonNull Interval interval)
        implements Serializable, Comparable<EventName> {

    public static EventName of(Event event) {
        return new EventName(event.getEventType(), event.getInterval());
    }

    @Override
    public int compareTo(EventName that) {
        Comparator<EventName> comparator = Comparator.comparing(EventName::interval);
        return Comparator.nullsFirst(comparator)
                .compare(this, that);
    }
}
