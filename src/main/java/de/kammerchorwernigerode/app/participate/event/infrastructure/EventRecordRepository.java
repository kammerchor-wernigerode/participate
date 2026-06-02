package de.kammerchorwernigerode.app.participate.event.infrastructure;

import de.kammerchorwernigerode.app.participate.event.presentation.model.EventProjection;
import de.kammerchorwernigerode.app.participate.event.presentation.model.details.attendee.AttendanceProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.Optional;

public interface EventRecordRepository extends JpaRepository<EventRecord, Long> {

    Optional<EventReference> findFirstByEndInstantGreaterThanEqualOrderByStartInstantAsc(Instant end);

    @EntityGraph(attributePaths = {"attendees"})
    Optional<AttendanceProjection> findSummaryById(Long id);

    Optional<EventProjection> findProjectionById(Long eventId);

    Optional<EventProjection> findFirstByStartInstantGreaterThanOrderByStartInstantAsc(Instant start);

    Optional<EventProjection> findFirstByStartInstantLessThanOrderByStartInstantDesc(Instant start);

    @Query("""
        select e.summary
        from EventRecord e
        where e.summary is not null
        group by e.summary
        order by count(e.summary) desc""")
    Page<String> findAllSummariesBySummaryNotNullGroupBySummaryOrderBySummaryCountDesc(Pageable pageable);
}
