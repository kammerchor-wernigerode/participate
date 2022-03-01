package de.vinado.wicket.participate.model;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import static de.vinado.wicket.participate.model.InvitationStatus.ACCEPTED;
import static de.vinado.wicket.participate.model.InvitationStatus.DECLINED;
import static de.vinado.wicket.participate.model.InvitationStatus.PENDING;
import static de.vinado.wicket.participate.model.InvitationStatus.UNINVITED;

/**
 * Entity of mapping between {@link Event} and {@link Singer}
 *
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
@Entity
@Table(name = "participants")
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Participant implements Identifiable<Long> {

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
    @Column(nullable = false)
    private InvitationStatus invitationStatus;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date fromDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date toDate;

    @Column(name = "need_catering")
    private boolean catering;

    @Column(name = "need_accommodation")
    private boolean accommodation;

    @Column(name = "car_seat_count")
    private Short carSeatCount;

    @Column(length = 65535, columnDefinition = "TEXT")
    private String comment;

    /**
     * @param event            {@link Event}
     * @param singer           {@link Singer}
     * @param token            Identifier token
     * @param invitationStatus {@link de.vinado.wicket.participate.model.InvitationStatus}
     * @param catering         Whether the participant wants to participate the restaurant
     * @param accommodation    Whether the participant needs an accommodation
     * @param comment          Comment overall
     */
    public Participant(final Event event, final Singer singer, final String token,
                       final InvitationStatus invitationStatus, final Date fromDate, final Date toDate,
                       final boolean catering, final boolean accommodation, short carSeatCount,
                       final String comment) {
        this.event = event;
        this.singer = singer;
        this.token = token;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.invitationStatus = invitationStatus;
        this.catering = catering;
        this.accommodation = accommodation;
        this.carSeatCount = carSeatCount;
        this.comment = comment;
    }

    public Participant(final Event event, final Singer singer, final String token,
                       final InvitationStatus invitationStatus) {
        this(event, singer, token, invitationStatus, null, null, false, false, (short) -1, null);
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
}
