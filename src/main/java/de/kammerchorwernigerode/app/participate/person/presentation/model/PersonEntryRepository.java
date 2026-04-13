package de.kammerchorwernigerode.app.participate.person.presentation.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PersonEntryRepository extends JpaRepository<PersonEntry, Long>, JpaSpecificationExecutor<PersonEntry> {
}
