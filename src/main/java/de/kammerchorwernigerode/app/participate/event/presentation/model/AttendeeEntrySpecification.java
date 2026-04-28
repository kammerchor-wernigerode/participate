package de.kammerchorwernigerode.app.participate.event.presentation.model;

import de.kammerchorwernigerode.app.participate.event.infrastructure.AttendeeRecord.InvitationStatus;
import de.kammerchorwernigerode.app.participate.event.infrastructure.AttendeeRecord_;
import de.kammerchorwernigerode.app.participate.musician.infrastructure.Voice;
import org.apache.wicket.model.IModel;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaBuilder.In;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class AttendeeEntrySpecification implements Specification<AttendeeEntry> {

    private final IModel<Long> eventId;

    private InvitationStatusSelection invitationStatusSelection = new InvitationStatusSelection();
    private String name;
    private List<Voice> voices = new ArrayList<>();

    @Override
    public Predicate toPredicate(Root<AttendeeEntry> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        return cb.and(eventIdEqual(root, query, cb),
            invitationStatusIn(root, query, cb),
            nameIlike(root, query, cb),
            voicesIn(root, query, cb));
    }

    private Predicate eventIdEqual(Root<AttendeeEntry> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        return cb.equal(root.get(AttendeeEntry_.id).get(AttendeeRecord_.Id_.eventId), this.eventId.getObject());
    }

    private Predicate invitationStatusIn(Root<AttendeeEntry> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        Set<InvitationStatus> invitationStatuses = getInvitationStatuses();
        if (invitationStatuses.isEmpty()) {
            return cb.conjunction();
        }

        In<InvitationStatus> in = cb.in(root.get(AttendeeEntry_.invitationStatus));
        for (InvitationStatus invitationStatus : invitationStatuses) {
            in.value(invitationStatus);
        }

        return in;
    }

    private Set<InvitationStatus> getInvitationStatuses() {
        Set<InvitationStatus> invitationStatuses = new HashSet<>();
        if (invitationStatusSelection.isUninvited()) {
            invitationStatuses.add(InvitationStatus.UNINVITED);
        }
        if (invitationStatusSelection.isTentative()) {
            invitationStatuses.add(InvitationStatus.TENTATIVE);
        }
        if (invitationStatusSelection.isAccepted()) {
            invitationStatuses.add(InvitationStatus.ACCEPTED);
        }
        if (invitationStatusSelection.isDeclined()) {
            invitationStatuses.add(InvitationStatus.DECLINED);
        }
        if (invitationStatusSelection.isPending()) {
            invitationStatuses.add(InvitationStatus.PENDING);
        }
        return invitationStatuses;
    }

    public Predicate nameIlike(Root<AttendeeEntry> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        if (!StringUtils.hasText(this.name)) {
            return cb.conjunction();
        }

        String[] tokens = Arrays.stream(this.name.toLowerCase().trim().split("\\s+"))
            .filter(StringUtils::hasText)
            .toArray(String[]::new);

        List<Predicate> perToken = new ArrayList<>();
        for (String token : tokens) {
            String pattern = "%" + escape(token) + "%";

            perToken.add(cb.or(
                cb.like(cb.lower(root.get(AttendeeEntry_.fileName)), pattern, '\\'),
                cb.like(cb.lower(root.get(AttendeeEntry_.firstName)), pattern, '\\'),
                cb.like(cb.lower(root.get(AttendeeEntry_.lastName)), pattern, '\\')
            ));
        }

        return cb.and(perToken.toArray(Predicate[]::new));
    }

    private Predicate voicesIn(Root<AttendeeEntry> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        if (CollectionUtils.isEmpty(this.voices)) {
            return cb.conjunction();
        }

        In<Voice> in = cb.in(root.get(AttendeeEntry_.voice));
        for (Voice voice : this.voices) {
            in.value(voice);
        }

        return in;
    }

    private static String escape(String input) {
        return input
            .replace("\\", "\\\\")
            .replace("%", "\\%")
            .replace("_", "\\_");
    }
}
