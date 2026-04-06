package de.kammerchorwernigerode.app.participate.event.presentation.ui;

import org.danekja.java.util.function.serializable.SerializableSupplier;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class EventEntrySpecification implements Specification<EventEntry> {

    private final SerializableSupplier<Instant> instant;

    private boolean all = false;

    @Override
    public Predicate toPredicate(Root<EventEntry> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        return cb.and(startInstantGreaterThanOrEqualTo(root, query, cb));
    }

    private Predicate startInstantGreaterThanOrEqualTo(Root<EventEntry> root, CriteriaQuery<?> query,
                                                       CriteriaBuilder cb) {
        if (this.all) {
            return cb.conjunction();
        }

        Instant instant = this.instant.get();
        if (null == instant) {
            return cb.disjunction();
        }

        return cb.greaterThanOrEqualTo(root.get(EventEntry_.startInstant), instant);
    }
}
