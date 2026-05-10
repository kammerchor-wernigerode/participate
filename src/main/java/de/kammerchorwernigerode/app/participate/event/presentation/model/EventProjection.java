package de.kammerchorwernigerode.app.participate.event.presentation.model;

import de.kammerchorwernigerode.app.participate.event.infrastructure.EventReference;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public interface EventProjection extends EventReference, Serializable {

    String getSummary();

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
