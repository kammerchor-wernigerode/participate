package de.kammerchorwernigerode.app.participate.event.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;

public interface EventRecordRepository extends JpaRepository<EventRecord, Long> {

    Optional<EventReference> findFirstByEndInstantGreaterThanEqualOrderByStartInstantAsc(Instant end);
}
