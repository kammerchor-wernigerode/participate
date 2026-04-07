package de.kammerchorwernigerode.app.participate.event.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.Optional;

public interface EventRecordRepository extends JpaRepository<EventRecord, Long> {

    @Query("""
        SELECT e.id
        FROM EventRecord e
        WHERE e.endInstant >= :end
        ORDER BY e.startInstant ASC
        LIMIT 1""")
    Optional<Long> findFirstIdByEndInstantGreaterThanEqualOrderByStartInstantAsc(Instant end);
}
