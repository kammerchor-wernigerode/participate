package de.kammerchorwernigerode.app.participate.event.presentation.model.details.attendee;

import de.kammerchorwernigerode.app.participate.event.infrastructure.AttendeeRecord.AccommodationStatus;
import de.kammerchorwernigerode.app.participate.event.infrastructure.AttendeeRecord.InvitationStatus;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

public interface AttendanceProjection {

    Long getId();

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

    List<Participation> getAttendees();


    interface Participation {

        InvitationStatus getInvitationStatus();

        AccommodationStatus getAccommodationStatus();

        LocalDateTime getFrom();

        LocalDateTime getTo();
    }
}
