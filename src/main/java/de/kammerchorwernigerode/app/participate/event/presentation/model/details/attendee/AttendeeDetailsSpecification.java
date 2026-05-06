package de.kammerchorwernigerode.app.participate.event.presentation.model.details.attendee;

import de.kammerchorwernigerode.app.participate.event.infrastructure.AttendeeRecord_;
import org.apache.wicket.model.IModel;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AttendeeDetailsSpecification implements Specification<AttendeeDetailsEntry> {

    private final IModel<Long> eventId;

    @Override
    public Predicate toPredicate(Root<AttendeeDetailsEntry> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        return cb.and(eventIdEqual(root, query, cb));
    }

    private Predicate eventIdEqual(Root<AttendeeDetailsEntry> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        return cb.equal(root.get(AttendeeDetailsEntry_.id).get(AttendeeRecord_.Id_.eventId), this.eventId.getObject());
    }
}
