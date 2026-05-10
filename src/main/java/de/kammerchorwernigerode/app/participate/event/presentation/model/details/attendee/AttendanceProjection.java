package de.kammerchorwernigerode.app.participate.event.presentation.model.details.attendee;

import de.kammerchorwernigerode.app.participate.event.infrastructure.AttendeeRecord.AccommodationStatus;
import de.kammerchorwernigerode.app.participate.event.infrastructure.AttendeeRecord.InvitationStatus;
import de.kammerchorwernigerode.app.participate.event.presentation.model.AttendeeProjection;
import de.kammerchorwernigerode.app.participate.event.presentation.model.EventProjection;

import java.io.Serializable;
import java.util.List;

public interface AttendanceProjection extends EventProjection, Serializable {

    List<Participation> getAttendees();


    interface Participation extends AttendeeProjection, Serializable {

        InvitationStatus getInvitationStatus();

        AccommodationStatus getAccommodationStatus();
    }
}
