package de.vinado.wicket.participate.data;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

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
import java.io.Serializable;
import java.util.Date;

/**
 * Entity of mapping between {@link Event} and {@link Singer}
 *
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
@Entity
@Table(name = "participants")
public class Participant implements Identifiable, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(name = "token", nullable = false, unique = true, length = 50)
    private String token;

    @ManyToOne
    @JoinColumn(name = "singer_id", nullable = false)
    private Singer singer;

    @Enumerated
    @Column(name = "invitation_status", nullable = false)
    private InvitationStatus invitationStatus;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "from_date")
    private Date fromDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "to_date")
    private Date toDate;

    @Column(name = "need_catering")
    private boolean catering;

    @Column(name = "need_accommodation")
    private boolean accommodation;

    @Column(name = "comment")
    private String comment;

    /**
     * Hibernate only
     */
    protected Participant() {
    }

    /**
     * @param event            {@link Event}
     * @param singer           {@link Singer}
     * @param token            Identifier token
     * @param invitationStatus {@link de.vinado.wicket.participate.data.InvitationStatus}
     * @param catering         Flag, if the singer want dinner
     * @param accommodation    Flag, if the singer needs a place to sleep
     * @param comment          Comment overall
     */
    public Participant(final Event event, final Singer singer, final String token,
                       final InvitationStatus invitationStatus, final Date fromDate, final Date toDate,
                       final boolean catering, final boolean accommodation, final String comment) {
        this.event = event;
        this.singer = singer;
        this.token = token;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.invitationStatus = invitationStatus;
        this.catering = catering;
        this.accommodation = accommodation;
        this.comment = comment;
    }

    public Participant(final Event event, final Singer singer, final String token,
                       final InvitationStatus invitationStatus) {
        this(event, singer, token, invitationStatus, null, null, false, false, null);
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(final Event event) {
        this.event = event;
    }

    public String getToken() {
        return token;
    }

    public void setToken(final String token) {
        this.token = token;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(final Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(final Date toDate) {
        this.toDate = toDate;
    }

    public Singer getSinger() {
        return singer;
    }

    public void setSinger(final Singer singer) {
        this.singer = singer;
    }

    public InvitationStatus getInvitationStatus() {
        return invitationStatus;
    }

    public void setInvitationStatus(final InvitationStatus invitationStatus) {
        this.invitationStatus = invitationStatus;
    }

    public boolean isCatering() {
        return catering;
    }

    public void setCatering(final boolean catering) {
        this.catering = catering;
    }

    public boolean isAccommodation() {
        return accommodation;
    }

    public void setAccommodation(final boolean accommodation) {
        this.accommodation = accommodation;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(final String comment) {
        this.comment = comment;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (!(o instanceof Participant)) return false;

        final Participant that = (Participant) o;

        return new EqualsBuilder()
            .append(catering, that.catering)
            .append(accommodation, that.accommodation)
            .append(id, that.id)
            .append(event, that.event)
            .append(token, that.token)
            .append(singer, that.singer)
            .append(invitationStatus, that.invitationStatus)
            .append(fromDate, that.fromDate)
            .append(toDate, that.toDate)
            .append(comment, that.comment)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
            .append(id)
            .append(event)
            .append(token)
            .append(singer)
            .append(invitationStatus)
            .append(fromDate)
            .append(toDate)
            .append(catering)
            .append(accommodation)
            .append(comment)
            .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("id", id)
            .append("event", event)
            .append("token", token)
            .append("singer", singer)
            .append("invitationStatus", invitationStatus)
            .append("fromDate", fromDate)
            .append("toDate", toDate)
            .append("catering", catering)
            .append("accommodation", accommodation)
            .append("comment", comment)
            .toString();
    }
}
