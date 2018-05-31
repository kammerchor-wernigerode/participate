package de.vinado.wicket.participate.data.filter;

import de.vinado.wicket.participate.data.InvitationStatus;
import de.vinado.wicket.participate.data.Voice;

import java.io.Serializable;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class MemberToEventFilter implements Serializable {

    private String searchTerm;

    private InvitationStatus invitationStatus;

    private Voice voice;

    private boolean notInvited;

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(final String searchTerm) {
        this.searchTerm = searchTerm;
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

    public boolean isNotInvited() {
        return notInvited;
    }

    public void setNotInvited(final boolean notInvited) {
        this.notInvited = notInvited;
    }
}
