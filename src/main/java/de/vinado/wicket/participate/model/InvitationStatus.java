package de.vinado.wicket.participate.model;

import de.vinado.wicket.participate.common.Sorted;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum InvitationStatus implements Sorted {
    ACCEPTED(2),
    DECLINED(3),
    PENDING(4),
    UNINVITED(0),
    TENTATIVE(1),
    ;

    private final int sortOrder;

    public static Predicate<Invitable> by(InvitationStatus invitationStatus) {
        return invitable -> Objects.equals(invitable.getInvitationStatus(), invitationStatus);
    }
}
