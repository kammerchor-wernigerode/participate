package de.kammerchorwernigerode.app.participate.event.presentation.model;

import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Immutable
@NoArgsConstructor
@Getter
@Subselect("""
    SELECT e.id                                                              AS id,
           e.summary                                                         AS summary,
           e.start_date_time                                                 AS start_date_time,
           e.start_zone                                                      AS start_zone,
           e.end_date_time                                                   AS end_date_time,
           e.end_zone                                                        AS end_zone,
           e.location                                                        AS location,
           e.description                                                     AS description,
           e.created_date_time                                               AS created_date_time,
           SUM(CASE WHEN a.invitation_status = 'ACCEPTED' THEN 1 ELSE 0 END) AS accepted_count,
           SUM(CASE WHEN a.invitation_status = 'DECLINED' THEN 1 ELSE 0 END) AS declined_count,
           SUM(CASE WHEN a.invitation_status = 'PENDING' THEN 1 ELSE 0 END)  AS pending_count
    FROM events e
             LEFT JOIN attendees a ON e.id = a.event_id
    GROUP BY e.id
    """)
@Synchronize({"events", "attendees"})
public class EventEntry implements Serializable {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @NonNull
    private Long id;

    @Column(name = "summary", updatable = false)
    @Nullable
    private String summary;

    @Column(name = "start_date_time", updatable = false)
    @NonNull
    private Instant startInstant;

    @Column(name = "start_zone", nullable = false)
    @NonNull
    private ZoneId startZoneId;

    @Column(name = "end_date_time", updatable = false)
    @NonNull
    private Instant endInstant;

    @Column(name = "end_zone", nullable = false)
    @NonNull
    private ZoneId endZoneId;

    @Column(name = "location", nullable = false)
    @Nullable
    private String location;

    @Column(name = "description")
    @Nullable
    private String description;

    @Column(name = "created_date_time", nullable = false)
    @NonNull
    private Instant createdDate;

    @Column(name = "accepted_count", nullable = false)
    private Long accepted;

    @Column(name = "declined_count", nullable = false)
    private Long declined;

    @Column(name = "pending_count", nullable = false)
    private Long pending;

    public ZonedDateTime getStart() {
        return ZonedDateTime.ofInstant(startInstant, startZoneId);
    }

    public ZonedDateTime getEnd() {
        return ZonedDateTime.ofInstant(endInstant, endZoneId);
    }
}
