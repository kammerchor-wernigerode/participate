package de.kammerchorwernigerode.app.participate.event.infrastructure;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Persistable;
import org.springframework.data.util.ProxyUtils;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Optional;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PostPersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "events")
@SequenceGenerator(name = "events_seq", sequenceName = "seq_events", allocationSize = 1)
@Data
@NoArgsConstructor
public class EventRecord implements Persistable<Long>, Comparable<EventRecord> {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "events_seq")
    private Long id;

    @Column(name = "summary")
    @Nullable
    private String summary;

    @Column(name = "start_date_time", nullable = false)
    @NonNull
    private Instant startInstant;

    @Column(name = "start_zone", nullable = false)
    @NonNull
    private ZoneId startZoneId;

    @Column(name = "end_date_time", nullable = false)
    @NonNull
    private Instant endInstant;

    @Column(name = "end_zone", nullable = false)
    @NonNull
    private ZoneId endZoneId;

    @Column(name = "location")
    @Nullable
    private String location;

    @Column(name = "description")
    @Nullable
    private String description;

    @CreationTimestamp
    @Column(name = "created_date_time", nullable = false, updatable = false)
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private Instant createdDate;

    @UpdateTimestamp
    @Column(name = "last_modified_date_time", nullable = false)
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private Instant lastModifiedDate;

    @Transient
    private boolean isNew;

    public EventRecord(@NonNull Long id, @NonNull Instant startInstant, @NonNull ZoneId startZoneId,
                       @NonNull Instant endInstant, @NonNull ZoneId endZoneId) {
        this(startInstant, startZoneId, endInstant, endZoneId);
        this.id = id;
        this.isNew = false;
    }

    public EventRecord(@NonNull Instant startInstant, @NonNull ZoneId startZoneId,
                       @NonNull Instant endInstant, @NonNull ZoneId endZoneId) {
        this.startInstant = startInstant;
        this.startZoneId = startZoneId;
        this.endInstant = endInstant;
        this.endZoneId = endZoneId;
        this.isNew = true;
    }

    public Optional<Instant> getCreatedDate() {
        return Optional.ofNullable(createdDate);
    }

    public Optional<Instant> getLastModifiedDate() {
        return Optional.ofNullable(lastModifiedDate);
    }

    @PostPersist
    protected void onPersist() {
        this.isNew = false;
    }

    @Override
    public int compareTo(EventRecord that) {
        return Comparator.comparing(EventRecord::getStartInstant)
            .thenComparing(EventRecord::getEndInstant)
            .compare(this, that);
    }

    // @checkstyle:off: NeedBraces
    @Override
    public boolean equals(Object obj) {
        if (null == obj) return false;
        if (this == obj) return true;
        if (!getClass().equals(ProxyUtils.getUserClass(obj))) return false;
        EventRecord that = (EventRecord) obj;
        return this.getId() != null && this.getId().equals(that.getId());
    }
    // @checkstyle:on: NeedBraces

    @Override
    public int hashCode() {
        int hashCode = 17;
        hashCode += null == getId() ? 0 : getId().hashCode() * 31;
        return hashCode;
    }

    @Override
    public String toString() {
        Class<?> userClass = ProxyUtils.getUserClass(this);
        return userClass.getSimpleName() + "(id=" + getId() + ")";
    }
}
