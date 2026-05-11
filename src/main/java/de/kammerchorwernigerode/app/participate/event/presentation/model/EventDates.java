package de.kammerchorwernigerode.app.participate.event.presentation.model;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public interface EventDates extends Serializable {

    Instant getStartInstant();

    default ZonedDateTime getStartDateTime() {
        return getStartInstant().atZone(ZoneOffset.UTC);
    }

    ZoneId getStartZoneId();

    Instant getEndInstant();

    default ZonedDateTime getEndDateTime() {
        return getEndInstant().atZone(ZoneOffset.UTC);
    }

    ZoneId getEndZoneId();
}
