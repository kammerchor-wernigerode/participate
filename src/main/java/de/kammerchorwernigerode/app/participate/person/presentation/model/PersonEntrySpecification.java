package de.kammerchorwernigerode.app.participate.person.presentation.model;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import lombok.Data;

@Data
public class PersonEntrySpecification implements Specification<PersonEntry> {

    private final boolean deleted = false;

    @Override
    public Predicate toPredicate(Root<PersonEntry> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        return cb.and(deletedEquals(root, query, cb));
    }

    private Predicate deletedEquals(Root<PersonEntry> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        return cb.equal(root.get(PersonEntry_.deleted), this.deleted);
    }
}
