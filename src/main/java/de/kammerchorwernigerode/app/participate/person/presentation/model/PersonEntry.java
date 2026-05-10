package de.kammerchorwernigerode.app.participate.person.presentation.model;

import de.kammerchorwernigerode.app.participate.musician.infrastructure.Voice;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Immutable
@NoArgsConstructor
@Getter
@Subselect("""
    SELECT p.id            AS id,
           p.first_name    AS first_name,
           p.last_name     AS last_name,
           p.file_name     AS file_name,
           p.email_address AS email_address,
           m.voice         AS voice,
           CASE
               WHEN m.voice = 'SOPRANO' THEN 0
               WHEN m.voice = 'ALTO' THEN 1
               WHEN m.voice = 'TENOR' THEN 2
               WHEN m.voice = 'BASS' THEN 3
               WHEN m.voice IS NULL THEN 4
               END         AS voice_order,
           m.is_deleted    AS is_deleted
    FROM persons AS p
             LEFT JOIN musicians AS m ON p.id = m.person_id
    """)
@Synchronize({"persons", "musicians"})
public class PersonEntry implements PersonProjection, Serializable {

    @Id
    @Column(name = "id")
    @NonNull
    private Long id;

    @Column(name = "first_name")
    @NonNull
    private String firstName;

    @Column(name = "last_name")
    @NonNull
    private String lastName;

    @Column(name = "file_name")
    @Nullable
    private String fileName;

    @Column(name = "email_address")
    @Nullable
    private String emailAddress;

    @Enumerated(EnumType.STRING)
    @Column(name = "voice")
    @Nullable
    private Voice voice;

    @Column(name = "voice_order")
    @NonNull
    private Integer voiceOrder;

    @Column(name = "is_deleted")
    private boolean deleted;
}
