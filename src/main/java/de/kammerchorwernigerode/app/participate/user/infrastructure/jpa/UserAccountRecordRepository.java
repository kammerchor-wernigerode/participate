package de.kammerchorwernigerode.app.participate.user.infrastructure.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAccountRecordRepository extends JpaRepository<UserAccountRecord, UserAccountRecord.Id> {
}
