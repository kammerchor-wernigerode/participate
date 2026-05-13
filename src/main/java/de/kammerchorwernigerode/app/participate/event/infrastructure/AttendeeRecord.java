package de.kammerchorwernigerode.app.participate.event.infrastructure;

import de.kammerchorwernigerode.app.participate.event.model.Accommodation;
import de.kammerchorwernigerode.app.participate.person.infrastructure.PersonRecord;
import de.kammerchorwernigerode.app.participate.util.Urns;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Persistable;
import org.springframework.data.util.ProxyUtils;

import java.io.Serializable;
import java.net.URI;
import java.time.LocalDateTime;
import java.util.UUID;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "attendees", indexes = {
    @Index(name = "uc_attendees_domain_key", columnList = "domain_key", unique = true),
    @Index(name = "uc_token", columnList = "token", unique = true),
})
@Data
@NoArgsConstructor
public class AttendeeRecord implements Persistable<AttendeeRecord.Id> {

    @EmbeddedId
    private Id id;

    @Column(name = "domain_key", unique = true, nullable = false)
    @NonNull
    private UUID domainKey;

    @MapsId("eventId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", referencedColumnName = "id",
        foreignKey = @ForeignKey(name = "fk_attendees_event_id"))
    @NonNull
    private EventRecord event;

    @MapsId("personId")
    @ManyToOne(optional = false)
    @JoinColumn(name = "person_id", referencedColumnName = "id",
        foreignKey = @ForeignKey(name = "fk_attendees_person_id"))
    @NonNull
    private PersonRecord person;

    @Deprecated
    @Column(name = "token", unique = true, insertable = false, updatable = false)
    @Nullable
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(name = "invitation_status", nullable = false)
    @NonNull
    private InvitationStatus invitationStatus;

    @Column(name = "from_date_time", nullable = false)
    @NonNull
    private LocalDateTime fromDateTime;

    @Column(name = "to_date_time", nullable = false)
    @NonNull
    private LocalDateTime toDateTime;

    @Column(name = "comment")
    @Nullable
    private String comment;

    @Column(name = "car_seat_count")
    @Nullable
    private Short carSeatCount;

    @Enumerated(EnumType.STRING)
    @Column(name = "accommodation_status")
    private Accommodation.@Nullable Status accommodationStatus;

    @Column(name = "accommodation_bed_count")
    @Nullable
    private Integer accommodationBedCount;

    @Transient
    private boolean isNew;

    @PostPersist
    protected void onPersist() {
        this.isNew = false;
    }

    // @checkstyle:off: NeedBraces
    @Override
    public boolean equals(Object obj) {
        if (null == obj) return false;
        if (this == obj) return true;
        if (!getClass().equals(ProxyUtils.getUserClass(obj))) return false;
        AttendeeRecord that = (AttendeeRecord) obj;
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


    @Embeddable
    public record Id(
        @Column(name = "event_id")
        Long eventId,

        @Column(name = "person_id")
        Long personId
    ) implements Serializable {

        public URI toUri() {
            String nss = "attendee:" + eventId;
            String fragment = personId.toString();
            return Urns.create(nss, fragment);
        }

        @NonNull
        @Override
        public String toString() {
            return toUri().toString();
        }
    }

    public enum InvitationStatus {

        UNINVITED,
        TENTATIVE,
        ACCEPTED,
        DECLINED,
        PENDING,
        ;
    }
}
