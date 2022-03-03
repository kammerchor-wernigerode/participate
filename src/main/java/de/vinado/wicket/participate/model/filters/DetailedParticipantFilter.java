package de.vinado.wicket.participate.model.filters;

import de.vinado.wicket.participate.model.InvitationStatus;
import de.vinado.wicket.participate.model.Participant;
import de.vinado.wicket.participate.model.Voice;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.function.Predicate;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
@Getter
@Setter
public class DetailedParticipantFilter implements Predicate<Participant>, Serializable {

    private String name;
    private String comment;
    private InvitationStatus invitationStatus;
    private Voice voice;
    private Date fromDate;
    private Date toDate;
    private boolean accommodation;
    private boolean catering;

    @Override
    public boolean test(Participant participant) {
        return matchesName(participant)
            && matchesComment(participant)
            && matchesInvitationStatus(participant)
            && matchesVoice(participant)
            && matchesFromDate(participant)
            && matchesToDate(participant)
            && matchesAccommodation(participant)
            && matchesCatering(participant);
    }

    private boolean matchesName(Participant participant) {
        return null == name || StringUtils.containsIgnoreCase(participant.getSinger().getDisplayName(), name);
    }

    private boolean matchesComment(Participant participant) {
        return null == comment || StringUtils.containsIgnoreCase(participant.getComment(), comment);
    }

    private boolean matchesInvitationStatus(Participant participant) {
        return null == invitationStatus || invitationStatus.equals(participant.getInvitationStatus());
    }

    private boolean matchesVoice(Participant participant) {
        return null == voice || voice.equals(participant.getSinger().getVoice());
    }

    private boolean matchesFromDate(Participant participant) {
        if (null == this.fromDate) return true;
        Date fromDate = participant.getFromDate();
        return this.fromDate.after(fromDate) || this.fromDate.equals(fromDate);
    }

    private boolean matchesToDate(Participant participant) {
        if (null == this.toDate) return true;
        Date toDate = participant.getToDate();
        return this.toDate.after(toDate) || this.toDate.equals(toDate);
    }

    private boolean matchesAccommodation(Participant participant) {
        if (accommodation) {
            return participant.isAccommodation();
        }
        return true;
    }

    private boolean matchesCatering(Participant participant) {
        if (catering) {
            return participant.isCatering();
        }
        return true;
    }
}
