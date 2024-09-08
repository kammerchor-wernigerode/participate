package de.vinado.wicket.participate.model;

import de.vinado.wicket.participate.common.Sorted;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum InvitationStatus implements Sorted {
    ACCEPTED(2, true, true),
    DECLINED(3, false, true),
    PENDING(4, false, false),
    UNINVITED(0, false, false),
    TENTATIVE(1, true, false),
    ;

    private final int sortOrder;
    private final boolean considerable;
    private final boolean definite;

    public static Predicate<Invitable> by(InvitationStatus invitationStatus) {
        return invitable -> Objects.equals(invitable.getInvitationStatus(), invitationStatus);
    }

    public static Stream<InvitationStatus> stream() {
        return Arrays.stream(values())
            .sorted(Sorted.compare());
    }
}
