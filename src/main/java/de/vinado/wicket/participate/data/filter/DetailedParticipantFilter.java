package de.vinado.wicket.participate.data.filter;

import de.vinado.wicket.participate.data.InvitationStatus;
import de.vinado.wicket.participate.data.Voice;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class DetailedParticipantFilter implements Serializable {

    private String name;

    private String comment;

    private InvitationStatus invitationStatus;

    private Voice voice;

    private Date fromDate;

    private Date toDate;

    private boolean accommodation;

    private boolean catering;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(final String comment) {
        this.comment = comment;
    }

    public InvitationStatus getInvitationStatus() {
        return invitationStatus;
    }

    public void setInvitationStatus(final InvitationStatus invitationStatus) {
        this.invitationStatus = invitationStatus;
    }

    public Voice getVoice() {
        return voice;
    }

    public void setVoice(final Voice voice) {
        this.voice = voice;
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

    public boolean isAccommodation() {
        return accommodation;
    }

    public void setAccommodation(final boolean accommodation) {
        this.accommodation = accommodation;
    }

    public boolean isCatering() {
        return catering;
    }

    public void setCatering(final boolean catering) {
        this.catering = catering;
    }
}
