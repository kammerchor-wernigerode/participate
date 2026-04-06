package de.kammerchorwernigerode.app.participate.event.presentation.model;

import de.kammerchorwernigerode.app.participate.event.infrastructure.AttendeeRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AttendeeEntryRepository extends JpaRepository<AttendeeEntry, AttendeeRecord.Id>,
    JpaSpecificationExecutor<AttendeeEntry> {
}
