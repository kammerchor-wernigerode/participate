package de.vinado.wicket.participate.data.filter;

import de.vinado.wicket.participate.data.InvitationStatus;
import de.vinado.wicket.participate.data.Voice;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class DetailedMemberToEventFilter implements Serializable {

    private String name;

    private String comment;

    private InvitationStatus invitationStatus;

    private Voice voice;

    private Date fromDate;

    private Date toDate;

    private boolean needsPlaceToSleep;

    private boolean needsDinner;

    private boolean notInvited;

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

    public boolean isNeedsPlaceToSleep() {
        return needsPlaceToSleep;
    }

    public void setNeedsPlaceToSleep(final boolean needsPlaceToSleep) {
        this.needsPlaceToSleep = needsPlaceToSleep;
    }

    public boolean isNeedsDinner() {
        return needsDinner;
    }

    public void setNeedsDinner(final boolean needsDinner) {
        this.needsDinner = needsDinner;
    }

    public boolean isNotInvited() {
        return notInvited;
    }

    public void setNotInvited(final boolean notInvited) {
        this.notInvited = notInvited;
    }
}
