package de.kammerchorwernigerode.app.participate.person.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRecordRepository extends JpaRepository<PersonRecord, Long> {
}
