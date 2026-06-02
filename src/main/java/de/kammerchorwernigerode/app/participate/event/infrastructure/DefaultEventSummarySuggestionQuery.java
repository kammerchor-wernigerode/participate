package de.kammerchorwernigerode.app.participate.event.infrastructure;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
@Profile("!postgresql")
class DefaultEventSummarySuggestionQuery implements EventSummarySuggestionQuery {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<String> execute(Payload payload) {
        return entityManager.createQuery("""
                select e.summary
                from EventRecord e
                where e.summary is not null
                  and e.summary ilike :query
                group by e.summary
                order by count(e.summary) desc""", String.class)
            .setParameter("query", "%" + payload.query() + "%")
            .setMaxResults(payload.pageable().getPageSize())
            .getResultList();
    }
}
