package de.kammerchorwernigerode.app.participate.event.presentation.model;

import de.kammerchorwernigerode.app.participate.event.infrastructure.AttendeeRecord_;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class AttendeeEntrySpecification implements Specification<AttendeeEntry> {

    private final Long eventId;

    @Override
    public Predicate toPredicate(Root<AttendeeEntry> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        return cb.and(eventIdEqual(root, query, cb));
    }

    private Predicate eventIdEqual(Root<AttendeeEntry> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        return cb.equal(root.get(AttendeeEntry_.id).get(AttendeeRecord_.Id_.eventId), this.eventId);
    }
}
