package de.kammerchorwernigerode.app.participate.event.presentation.model.details.attendee;

import de.kammerchorwernigerode.app.participate.event.infrastructure.AttendeeRecord.InvitationStatus;
import de.kammerchorwernigerode.app.participate.event.presentation.model.EventDates;
import de.kammerchorwernigerode.app.participate.musician.infrastructure.Voice;
import de.kammerchorwernigerode.app.participate.person.presentation.model.PersonProjection;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
@Subselect("""
    SELECT a.event_id          AS event_id,
           a.person_id         AS person_id,
           p.file_name         AS file_name,
           p.first_name        AS first_name,
           p.last_name         AS last_name,
           a.invitation_status AS invitation_status,
           m.voice             AS voice,
           CASE
               WHEN a.accommodation_status = 'SEARCHING' THEN true
               ELSE false
               END             AS needs_accommodation,
           CASE
               WHEN a.accommodation_status = 'OFFERING' THEN a.accommodation_bed_count
               ELSE 0
               END             AS beds_offered_count,
           CASE
               WHEN a.car_seat_count >= 0 THEN true
               ELSE false
               END             AS by_car,
           CASE
               WHEN a.car_seat_count >= 0 THEN a.car_seat_count
               ELSE 0
               END             AS car_seat_count,
           a.comment           AS comment,
           a.from_date_time    AS from_date_time,
           a.to_date_time      AS to_date_time,
           e.start_date_time   AS start_date_time,
           e.start_zone        AS start_zone,
           e.end_date_time     AS end_date_time,
           e.end_zone          AS end_zone,
           CASE
               WHEN a.invitation_status = 'TENTATIVE' THEN 0
               WHEN a.invitation_status = 'ACCEPTED' THEN 0
               WHEN a.invitation_status = 'DECLINED' THEN 0
               ELSE 1
               END             AS invitation_status_order,
           CASE
               WHEN m.voice IS NULL THEN 0
               WHEN m.voice = 'SOPRANO' THEN 1
               WHEN m.voice = 'ALTO' THEN 2
               WHEN m.voice = 'TENOR' THEN 3
               WHEN m.voice = 'BASS' THEN 4
               END             AS voice_order
    FROM attendees AS a
             JOIN events AS e ON a.event_id = e.id
             JOIN persons AS p ON a.person_id = p.id
             LEFT JOIN musicians AS m ON p.id = m.person_id
    """)
@Synchronize({"attendees", "persons", "musicians"})
@NoArgsConstructor
@Getter
public class AttendeeDetailsEntry implements PersonProjection, EventDates, Serializable {

    @EmbeddedId
    @NonNull
    private Id id;

    @Column(name = "file_name")
    @Nullable
    private String fileName;

    @Column(name = "first_name")
    @NonNull
    private String firstName;

    @Column(name = "last_name")
    @NonNull
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(name = "invitation_status")
    @NonNull
    private InvitationStatus invitationStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "voice")
    @Nullable
    private Voice voice;

    @Column(name = "needs_accommodation")
    private boolean accommodationNeeded;

    @Column(name = "beds_offered_count")
    private int bedsOfferedCount;

    @Column(name = "by_car")
    private boolean byCar;

    @Column(name = "car_seat_count")
    private int carSeatCount;

    @Column(name = "comment")
    @Nullable
    private String comment;

    @Column(name = "from_date_time")
    @NonNull
    private LocalDateTime fromDateTime;

    @Column(name = "to_date_time")
    @NonNull
    private LocalDateTime toDateTime;

    @Column(name = "start_date_time")
    @NonNull
    private Instant startInstant;

    @Column(name = "start_zone")
    @NonNull
    private ZoneId startZoneId;

    @Column(name = "end_date_time")
    @NonNull
    private Instant endInstant;

    @Column(name = "end_zone")
    @NonNull
    private ZoneId endZoneId;

    @Column(name = "invitation_status_order")
    private int invitationStatusOrder;

    @Column(name = "voice_order")
    private int voiceOrder;
}
