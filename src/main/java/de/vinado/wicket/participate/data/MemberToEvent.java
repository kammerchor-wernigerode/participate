package de.vinado.wicket.participate.data;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
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
 * Entity of mapping between {@link Event} and {@link Member}
 *
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
@Entity
@Table(name = "m_member_event")
public class MemberToEvent implements Identifiable, Serializable {

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
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(name = "invitation_status_id")
    private InvitationStatus invitationStatus;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "from_date")
    private Date fromDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "to_date")
    private Date toDate;

    @Column(name = "needs_dinner")
    private boolean needsDinner;

    @Column(name = "dinner_comment")
    private String needsDinnerComment;

    @Column(name = "needs_place_to_sleep")
    private boolean needsPlaceToSleep;

    @Column(name = "needs_place_to_sleep_comment")
    private String needsPlaceToSleepComment;

    @Column(name = "comment")
    private String comment;

    @Column(name = "is_reviewed")
    private boolean reviewed;

    @Column(name = "is_invited", nullable = false)
    private boolean invited;

    /**
     * Hibernate only
     */
    protected MemberToEvent() {
    }

    /**
     * @param event                    {@link Event}
     * @param member                   {@link Member}
     * @param token                    Identifier token
     * @param invitationStatus         {@link de.vinado.wicket.participate.data.InvitationStatus}
     * @param needsDinner              Flag, if the member want dinner
     * @param needsDinnerComment       Dinner comment
     * @param needsPlaceToSleep        Flag, if the member needs a place to sleep
     * @param needsPlaceToSleepComment Place to sleep comment
     * @param comment                  Comment overall
     */
    public MemberToEvent(final Event event, final Member member, final String token,
                         final InvitationStatus invitationStatus, final Date fromDate, final Date toDate,
                         final boolean needsDinner, final String needsDinnerComment, final boolean needsPlaceToSleep,
                         final String needsPlaceToSleepComment, final String comment) {
        this.event = event;
        this.member = member;
        this.token = token;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.invitationStatus = invitationStatus;
        this.needsDinner = needsDinner;
        this.needsDinnerComment = needsDinnerComment;
        this.needsPlaceToSleep = needsPlaceToSleep;
        this.needsPlaceToSleepComment = needsPlaceToSleepComment;
        this.comment = comment;
        this.reviewed = false;
        this.invited = false;
    }

    public MemberToEvent(final Event event, final Member member, final String token,
                         final InvitationStatus invitationStatus) {
        this(event, member, token, invitationStatus, null, null, false, null, false, null, null);
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

    public Member getMember() {
        return member;
    }

    public void setMember(final Member member) {
        this.member = member;
    }

    public InvitationStatus getInvitationStatus() {
        return invitationStatus;
    }

    public void setInvitationStatus(final InvitationStatus invitationStatus) {
        this.invitationStatus = invitationStatus;
    }

    public boolean isNeedsDinner() {
        return needsDinner;
    }

    public void setNeedsDinner(final boolean needsDinner) {
        this.needsDinner = needsDinner;
    }

    public String getNeedsDinnerComment() {
        return needsDinnerComment;
    }

    public void setNeedsDinnerComment(final String needsDinnerComment) {
        this.needsDinnerComment = needsDinnerComment;
    }

    public boolean isNeedsPlaceToSleep() {
        return needsPlaceToSleep;
    }

    public void setNeedsPlaceToSleep(final boolean needsPlaceToSleep) {
        this.needsPlaceToSleep = needsPlaceToSleep;
    }

    public String getNeedsPlaceToSleepComment() {
        return needsPlaceToSleepComment;
    }

    public void setNeedsPlaceToSleepComment(final String needsPlaceToSleepComment) {
        this.needsPlaceToSleepComment = needsPlaceToSleepComment;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(final String comment) {
        this.comment = comment;
    }

    public boolean isReviewed() {
        return reviewed;
    }

    public void setReviewed(final boolean reviewed) {
        this.reviewed = reviewed;
    }

    public boolean isInvited() {
        return invited;
    }

    public void setInvited(final boolean invited) {
        this.invited = invited;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (!(o instanceof MemberToEvent)) return false;

        final MemberToEvent that = (MemberToEvent) o;

        return new EqualsBuilder()
                .append(needsDinner, that.needsDinner)
                .append(needsPlaceToSleep, that.needsPlaceToSleep)
                .append(reviewed, that.reviewed)
                .append(invited, that.invited)
                .append(id, that.id)
                .append(event, that.event)
                .append(token, that.token)
                .append(member, that.member)
                .append(invitationStatus, that.invitationStatus)
                .append(fromDate, that.fromDate)
                .append(toDate, that.toDate)
                .append(needsDinnerComment, that.needsDinnerComment)
                .append(needsPlaceToSleepComment, that.needsPlaceToSleepComment)
                .append(comment, that.comment)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(event)
                .append(token)
                .append(member)
                .append(invitationStatus)
                .append(fromDate)
                .append(toDate)
                .append(needsDinner)
                .append(needsDinnerComment)
                .append(needsPlaceToSleep)
                .append(needsPlaceToSleepComment)
                .append(comment)
                .append(reviewed)
                .append(invited)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("event", event)
                .append("token", token)
                .append("member", member)
                .append("invitationStatus", invitationStatus)
                .append("fromDate", fromDate)
                .append("toDate", toDate)
                .append("needsDinner", needsDinner)
                .append("needsDinnerComment", needsDinnerComment)
                .append("needsPlaceToSleep", needsPlaceToSleep)
                .append("needsPlaceToSleepComment", needsPlaceToSleepComment)
                .append("comment", comment)
                .append("reviewed", reviewed)
                .append("invited", invited)
                .toString();
    }
}
