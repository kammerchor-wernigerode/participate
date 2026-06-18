package de.kammerchorwernigerode.app.participate.person.infrastructure;

import de.kammerchorwernigerode.app.participate.person.presentation.model.PersonPickProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.util.Streamable;

public interface PersonRecordRepository extends JpaRepository<PersonRecord, Long> {

    boolean existsByUserEmailAddress(String emailAddress);

    boolean existsByFileNameIgnoreCase(String fileName);

    boolean existsByEmailAddressIgnoreCase(String fileName);

    Streamable<PersonPickProjection> findAllPersonPickProjectionsByMusicianDeletedIsFalse();
}
