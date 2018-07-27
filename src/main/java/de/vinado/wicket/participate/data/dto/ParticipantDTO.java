package de.vinado.wicket.participate.data.dto;

import de.vinado.wicket.participate.data.Event;
import de.vinado.wicket.participate.data.InvitationStatus;
import de.vinado.wicket.participate.data.Member;
import de.vinado.wicket.participate.data.Participant;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class ParticipantDTO implements Serializable {

    private Participant participant;

    private Event event;

    private Member member;

    private String token;

    private Date fromDate;

    private Date toDate;

    private InvitationStatus invitationStatus;

    private boolean needsDinner;

    private String needsDinnerComment;

    private boolean needsPlaceToSleep;

    private String needsPlaceToSleepComment;

    private String comment;

    private boolean reviewed;

    public ParticipantDTO() {
    }

    public ParticipantDTO(final Participant participant) {
        this.participant = participant;
        this.event = participant.getEvent();
        this.member = participant.getMember();
        this.token = participant.getToken();
        this.fromDate = participant.getFromDate();
        this.toDate = participant.getToDate();
        this.invitationStatus = participant.getInvitationStatus();
        this.needsDinner = participant.isNeedsDinner();
        this.needsDinnerComment = participant.getNeedsDinnerComment();
        this.needsPlaceToSleep = participant.isNeedsPlaceToSleep();
        this.needsPlaceToSleepComment = participant.getNeedsPlaceToSleepComment();
        this.comment = participant.getComment();
        this.reviewed = participant.isReviewed();
    }

    public Participant getParticipant() {
        return participant;
    }

    public void setParticipant(final Participant participant) {
        this.participant = participant;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(final Event event) {
        this.event = event;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(final Member member) {
        this.member = member;
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
}
