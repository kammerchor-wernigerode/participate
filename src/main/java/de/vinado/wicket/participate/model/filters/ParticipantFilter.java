package de.vinado.wicket.participate.model.filters;

import de.vinado.wicket.participate.model.InvitationStatus;
import de.vinado.wicket.participate.model.Participant;
import de.vinado.wicket.participate.model.Voice;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.danekja.java.util.function.serializable.SerializablePredicate;

import java.util.Date;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
@Getter
@Setter
public class ParticipantFilter implements SerializablePredicate<Participant> {

    private String name;
    private String comment;
    private InvitationStatus invitationStatus;
    private Voice voice;
    private Date fromDate;
    private Date toDate;
    private boolean accommodation;
    private boolean catering;
    private boolean notInvited;

    @Override
    public boolean test(Participant participant) {
        return matchesName(participant)
            && matchesComment(participant)
            && matchesInvitationStatus(participant)
            && matchesVoice(participant)
            && matchesFromDate(participant)
            && matchesToDate(participant)
            && matchesAccommodation(participant)
            && matchesCatering(participant)
            && matchesNotInvited(participant);
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
        Date fromDate = participant.getFromDate();
        if (null == this.fromDate || null == fromDate) return true;
        return this.fromDate.after(fromDate) || this.fromDate.equals(fromDate);
    }

    private boolean matchesToDate(Participant participant) {
        Date toDate = participant.getToDate();
        if (null == this.toDate || null == toDate) return true;
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

    private boolean matchesNotInvited(Participant participant) {
        if (notInvited) {
            return participant.isUninvited();
        }
        return true;
    }
}
