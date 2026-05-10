package de.kammerchorwernigerode.app.participate.event.presentation.model.details.attendee;

import de.kammerchorwernigerode.app.participate.event.infrastructure.AttendeeRecord.Id;
import de.kammerchorwernigerode.app.participate.event.infrastructure.AttendeeRecord.InvitationStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AttendeeDto implements Serializable {

    private final Id id;
    private InvitationStatus invitationStatus;
    private boolean accommodationNeeded;
    private int bedsOfferedCount;
    private boolean byCar;
    private int carSeatCount;
    private String comment;
    private LocalDateTime fromDateTime;
    private LocalDateTime toDateTime;
}
