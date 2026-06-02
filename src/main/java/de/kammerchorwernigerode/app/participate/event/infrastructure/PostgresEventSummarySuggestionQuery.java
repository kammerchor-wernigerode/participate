package de.kammerchorwernigerode.app.participate.event.infrastructure;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Repository
@Profile("postgresql")
class PostgresEventSummarySuggestionQuery implements EventSummarySuggestionQuery {

    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    @Override
    public List<String> execute(Payload payload) {
        Query sqlQuery = entityManager
            .createNativeQuery("""
                select e.summary
                from events e
                where e.summary is not null
                  and e.summary ilike '%' || :query || '%'
                group by e.summary
                order by similarity(e.summary, :query) desc""", String.class)
            .setParameter("query", payload.query())
            .setMaxResults(payload.pageable().getPageSize());
        return (List<String>) sqlQuery.getResultList();
    }
}
