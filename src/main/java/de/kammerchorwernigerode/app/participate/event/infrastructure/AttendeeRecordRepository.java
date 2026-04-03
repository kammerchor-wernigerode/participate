package de.kammerchorwernigerode.app.participate.event.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttendeeRecordRepository extends JpaRepository<AttendeeRecord, AttendeeRecord.Id> {

    List<AttendeeRecord> findAllByDomainKeyIsNull();
}
