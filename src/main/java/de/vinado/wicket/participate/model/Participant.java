package de.vinado.wicket.participate.model;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

import static de.vinado.wicket.participate.model.InvitationStatus.ACCEPTED;
import static de.vinado.wicket.participate.model.InvitationStatus.DECLINED;
import static de.vinado.wicket.participate.model.InvitationStatus.PENDING;
import static de.vinado.wicket.participate.model.InvitationStatus.TENTATIVE;
import static de.vinado.wicket.participate.model.InvitationStatus.UNINVITED;

@Entity
@Table(name = "participants")
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Participant implements Identifiable<Long>, Invitable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(nullable = false, unique = true, length = 50)
    private String token;

    @ManyToOne
    @JoinColumn(name = "singer_id", nullable = false)
    private Singer singer;

    @Enumerated
    @Column(nullable = false, columnDefinition = "integer")
    private InvitationStatus invitationStatus;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date fromDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date toDate;

    @Column(name = "car_seat_count")
    private Short carSeatCount;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "status", column = @Column(name = "accommodation_status")),
        @AttributeOverride(name = "beds", column = @Column(name = "accommodation_bed_count")),
    })
    private Accommodation accommodation = new Accommodation();

    @MapsId("event")
    @ManyToOne
    @JoinColumn(name = "event_id", insertable = false, updatable = false)
    private EventDetails eventDetails;

    public Participant(Event event, Singer singer, String token,
                       InvitationStatus invitationStatus, Date fromDate, Date toDate,
                       Accommodation accommodation, short carSeatCount,
                       String comment) {
        this.event = event;
        this.singer = singer;
        this.token = token;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.invitationStatus = invitationStatus;
        this.accommodation = accommodation;
        this.carSeatCount = carSeatCount;
        this.comment = comment;
    }

    public Participant(Event event, Singer singer, String token,
                       InvitationStatus invitationStatus) {
        this(event, singer, token, invitationStatus, null, null, new Accommodation(), (short) -1, null);
    }

    public boolean isUninvited() {
        return UNINVITED.equals(invitationStatus);
    }

    public boolean isPending() {
        return PENDING.equals(invitationStatus);
    }

    public boolean isAccepted() {
        return ACCEPTED.equals(invitationStatus);
    }

    public boolean isDeclined() {
        return DECLINED.equals(invitationStatus);
    }

    public boolean isTentative() {
        return TENTATIVE.equals(invitationStatus);
    }

    @Transient
    public boolean isConsiderable() {
        return invitationStatus.isConsiderable();
    }
}
