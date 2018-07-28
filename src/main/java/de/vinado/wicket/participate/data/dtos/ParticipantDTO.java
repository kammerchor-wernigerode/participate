package de.vinado.wicket.participate.data.dtos;

import de.vinado.wicket.participate.data.Event;
import de.vinado.wicket.participate.data.InvitationStatus;
import de.vinado.wicket.participate.data.Participant;
import de.vinado.wicket.participate.data.Singer;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class ParticipantDTO implements Serializable {

    private Participant participant;

    private Event event;

    private Singer singer;

    private String token;

    private Date fromDate;

    private Date toDate;

    private InvitationStatus invitationStatus;

    private boolean catering;

    private boolean accommodation;

    private String comment;

    public ParticipantDTO() {
    }

    public ParticipantDTO(final Participant participant) {
        this.participant = participant;
        this.event = participant.getEvent();
        this.singer = participant.getSinger();
        this.token = participant.getToken();
        this.fromDate = participant.getFromDate();
        this.toDate = participant.getToDate();
        this.invitationStatus = participant.getInvitationStatus();
        this.catering = participant.isCatering();
        this.accommodation = participant.isAccommodation();
        this.comment = participant.getComment();
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

    public Singer getSinger() {
        return singer;
    }

    public void setSinger(final Singer singer) {
        this.singer = singer;
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
}
