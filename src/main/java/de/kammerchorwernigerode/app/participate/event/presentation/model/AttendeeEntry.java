package de.kammerchorwernigerode.app.participate.event.presentation.model;

import de.kammerchorwernigerode.app.participate.event.infrastructure.AttendeeRecord.InvitationStatus;
import de.kammerchorwernigerode.app.participate.musician.infrastructure.Voice;
import de.kammerchorwernigerode.app.participate.person.presentation.model.PersonProjection;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.Serializable;
import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import lombok.Getter;
import lombok.NoArgsConstructor;

import static de.kammerchorwernigerode.app.participate.event.infrastructure.AttendeeRecord.Id;

@Entity
@Immutable
@NoArgsConstructor
@Getter
@Subselect("""
    SELECT a.event_id                AS event_id,
           a.person_id               AS person_id,
           a.invitation_status       AS invitation_status,
           p.first_name              AS first_name,
           p.last_name               AS last_name,
           p.file_name               AS file_name,
           m.voice                   AS voice,
           a.comment                 AS comment,
           a.from_date_time          AS from_date_time,
           a.to_date_time            AS to_date_time,
           a.car_seat_count          AS car_seat_count,
           CASE
               WHEN a.invitation_status = 'UNINVITED' THEN 0
               WHEN a.invitation_status = 'TENTATIVE' THEN 1
               WHEN a.invitation_status = 'ACCEPTED' THEN 2
               WHEN a.invitation_status = 'DECLINED' THEN 3
               WHEN a.invitation_status = 'PENDING' THEN 4
               END                   AS invitation_status_order,
           CASE
               WHEN m.voice IS NULL THEN 0
               WHEN m.voice = 'SOPRANO' THEN 1
               WHEN m.voice = 'ALTO' THEN 2
               WHEN m.voice = 'TENOR' THEN 3
               WHEN m.voice = 'BASS' THEN 4
               END                   AS voice_order
    FROM attendees AS a
             JOIN persons AS p ON a.person_id = p.id
             LEFT JOIN musicians AS m ON p.id = m.person_id
    """)
@Synchronize({"attendees"})
public class AttendeeEntry implements PersonProjection, AttendeeProjection, AttendeeProjection.Attributes,
    Serializable {

    @EmbeddedId
    @NonNull
    private Id id;

    @Enumerated(EnumType.STRING)
    @Column(name = "invitation_status", insertable = false, updatable = false)
    @NonNull
    private InvitationStatus invitationStatus;

    @Column(name = "first_name", insertable = false, updatable = false)
    @NonNull
    private String firstName;

    @Column(name = "last_name", insertable = false, updatable = false)
    @NonNull
    private String lastName;

    @Column(name = "file_name", insertable = false, updatable = false)
    @Nullable
    private String fileName;

    @Enumerated(EnumType.STRING)
    @Column(name = "voice", insertable = false, updatable = false)
    @Nullable
    private Voice voice;

    @Column(name = "comment")
    @Nullable
    private String comment;

    @Column(name = "from_date_time")
    @NonNull
    private LocalDateTime fromDateTime;

    @Column(name = "to_date_time")
    @NonNull
    private LocalDateTime toDateTime;

    @Column(name = "car_seat_count")
    @Nullable
    private Short carSeatCount;

    @Column(name = "invitation_status_order", insertable = false, updatable = false)
    @NonNull
    private Integer invitationStatusOrder;

    @Column(name = "voice_order", insertable = false, updatable = false)
    @NonNull
    private Integer voiceOrder;
}
