package de.vinado.wicket.participate.person.infrastructure;

import de.vinado.wicket.participate.model.Person;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

interface PersonRecordRepository extends JpaRepository<Person, Long> {

    @Modifying
    @Query("UPDATE Singer s SET s.active = true WHERE s.id = :#{#person.id}")
    void restore(@NonNull Person person);
}
