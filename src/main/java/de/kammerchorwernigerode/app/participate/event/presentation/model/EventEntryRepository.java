package de.kammerchorwernigerode.app.participate.event.presentation.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EventEntryRepository extends JpaRepository<EventEntry, Long>, JpaSpecificationExecutor<EventEntry> {
}
