package de.kammerchorwernigerode.app.participate.person.presentation.model;

import de.kammerchorwernigerode.app.participate.musician.infrastructure.Voice;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import lombok.Data;

@Data
public class PersonEntrySpecification implements Specification<PersonEntry> {

    private final boolean deleted = false;

    private String name;
    private Voice voice;
    private String emailAddress;

    @Override
    public Predicate toPredicate(Root<PersonEntry> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        return cb.and(deletedEquals(root, query, cb),
            textIlike(root, query, cb),
            voiceEquals(root, query, cb),
            emailIlike(root, query, cb));
    }

    private Predicate deletedEquals(Root<PersonEntry> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        return cb.equal(root.get(PersonEntry_.deleted), this.deleted);
    }

    public Predicate textIlike(Root<PersonEntry> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        if (!StringUtils.hasText(this.name)) {
            return cb.conjunction();
        }

        String[] tokens = Arrays.stream(this.name.trim().split("\\s+"))
            .filter(StringUtils::hasText)
            .toArray(String[]::new);

        Expression<String> fileName = cb.lower(root.get(PersonEntry_.fileName));
        Expression<String> firstName = cb.lower(root.get(PersonEntry_.firstName));
        Expression<String> lastName = cb.lower(root.get(PersonEntry_.lastName));

        List<Predicate> perToken = new ArrayList<>();
        for (String token : tokens) {
            String pattern = "%" + escape(token) + "%";

            perToken.add(cb.or(
                cb.like(fileName, pattern, '\\'),
                cb.like(firstName, pattern, '\\'),
                cb.like(lastName, pattern, '\\')
            ));
        }

        return cb.and(perToken.toArray(Predicate[]::new));
    }

    private Predicate voiceEquals(Root<PersonEntry> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        return null == voice
            ? cb.conjunction()
            : cb.equal(root.get(PersonEntry_.voice), this.voice);
    }

    private Predicate emailIlike(Root<PersonEntry> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        return StringUtils.hasText(this.emailAddress)
            ? like(root.get(PersonEntry_.emailAddress), this.emailAddress, cb)
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
