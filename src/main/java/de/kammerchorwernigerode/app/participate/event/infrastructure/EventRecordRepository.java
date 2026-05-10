package de.kammerchorwernigerode.app.participate.event.infrastructure;

import de.kammerchorwernigerode.app.participate.event.presentation.model.EventProjection;
import de.kammerchorwernigerode.app.participate.event.presentation.model.details.attendee.AttendanceProjection;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;

public interface EventRecordRepository extends JpaRepository<EventRecord, Long> {

    Optional<EventReference> findFirstByEndInstantGreaterThanEqualOrderByStartInstantAsc(Instant end);

    @EntityGraph(attributePaths = {"attendees"})
    Optional<AttendanceProjection> findSummaryById(Long id);

    Optional<EventProjection> findProjectionById(Long eventId);

    Optional<EventProjection> findFirstByStartInstantGreaterThanOrderByStartInstantAsc(Instant start);

    Optional<EventProjection> findFirstByStartInstantLessThanOrderByStartInstantDesc(Instant start);
}
