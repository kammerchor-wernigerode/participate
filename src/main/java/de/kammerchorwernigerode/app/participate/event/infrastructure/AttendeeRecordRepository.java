package de.kammerchorwernigerode.app.participate.event.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendeeRecordRepository extends JpaRepository<AttendeeRecord, AttendeeRecord.Id> {
}
