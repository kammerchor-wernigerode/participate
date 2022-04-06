package de.vinado.wicket.participate.model;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public enum InvitationStatus {
    ACCEPTED,
    DECLINED,
    PENDING,
    UNINVITED,
    TENTATIVE,
    ;

    public static Predicate<Participant> by(InvitationStatus invitationStatus) {
        return participant -> Objects.equals(participant.getInvitationStatus(), invitationStatus);
    }
}
