package de.kammerchorwernigerode.app.participate.user.infrastructure.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRecordRepository extends JpaRepository<UserRecord, Long> {

    Optional<UserRecord> findByEmailAddress(String emailAddress);
}
