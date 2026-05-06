package de.kammerchorwernigerode.app.participate.event.presentation.model.details.attendee;

import de.kammerchorwernigerode.app.participate.event.infrastructure.AttendeeRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AttendeeDetailsEntryRepository extends JpaRepository<AttendeeDetailsEntry, AttendeeRecord.Id>,
    JpaSpecificationExecutor<AttendeeDetailsEntry> {
}
