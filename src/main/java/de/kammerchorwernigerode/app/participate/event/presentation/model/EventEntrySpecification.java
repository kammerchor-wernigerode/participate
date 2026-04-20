package de.kammerchorwernigerode.app.participate.event.presentation.model;

import org.danekja.java.util.function.serializable.SerializableSupplier;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Optional;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class EventEntrySpecification implements Specification<EventEntry> {

    private final SerializableSupplier<Instant> instant;

    private boolean all = false;
    private String summary;
    private DateRange dateRange = new DateRange();
    private String location;

    @Override
    public Predicate toPredicate(Root<EventEntry> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        return cb.and(endInstantGreaterThanOrEqualTo(root, query, cb),
            summaryIlike(root, query, cb),
            startEndBetween(root, query, cb),
            locationIlike(root, query, cb));
    }

    private Predicate endInstantGreaterThanOrEqualTo(Root<EventEntry> root, CriteriaQuery<?> query,
                                                     CriteriaBuilder cb) {
        if (this.all) {
            return cb.conjunction();
        }

        Instant instant = this.instant.get();
        if (null == instant) {
            return cb.disjunction();
        }

        return cb.greaterThanOrEqualTo(root.get(EventEntry_.endInstant), instant);
    }

    private Predicate summaryIlike(Root<EventEntry> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        return StringUtils.hasText(this.summary)
            ? like(cb.lower(root.get(EventEntry_.summary)), this.summary.toLowerCase(), cb)
            : cb.conjunction();
    }

    private Predicate startEndBetween(Root<EventEntry> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        if (null == this.dateRange) {
            return cb.conjunction();
        }

        Instant start = Optional.ofNullable(this.dateRange.getStartDateTime())
            .map(self -> Instant.from(self.atZone(this.dateRange.getStartZoneId())))
            .orElse(null);
        Instant end = Optional.ofNullable(this.dateRange.getEndDateTime())
            .map(self -> Instant.from(self.atZone(this.dateRange.getEndZoneId())))
            .orElse(null);
        if (null == start && null == end) {
            return cb.conjunction();
        }

        Predicate greaterThanOrEqualToFrom = cb.greaterThanOrEqualTo(root.get(EventEntry_.startInstant), start);
        Predicate lessThanOrEqualToTo = cb.lessThanOrEqualTo(root.get(EventEntry_.endInstant), end);
        if (null != start && null != end) {
            return cb.and(greaterThanOrEqualToFrom, lessThanOrEqualToTo);
        } else if (null == start) {
            return lessThanOrEqualToTo;
        } else {
            return greaterThanOrEqualToFrom;
        }
    }

    private Predicate locationIlike(Root<EventEntry> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        return StringUtils.hasText(this.location)
            ? like(cb.lower(root.get(EventEntry_.location)), this.location.toLowerCase(), cb)
            : cb.conjunction();
    }

    private static Predicate like(Expression<String> field, String input, CriteriaBuilder cb) {
        return cb.like(field, "%" + escape(input) + "%", '\\');
    }

    private static String escape(String input) {
        return input
            .replace("\\", "\\\\")
            .replace("%", "\\%")
            .replace("_", "\\_");
    }
}
