package de.kammerchorwernigerode.app.participate.person.infrastructure;

import de.kammerchorwernigerode.app.participate.musician.infrastructure.MusicianRecord;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Persistable;
import org.springframework.data.util.ProxyUtils;

import java.time.Instant;
import java.util.Optional;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PostPersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "persons", uniqueConstraints = {
    @UniqueConstraint(name = "uc_email_address", columnNames = "email_address"),
    @UniqueConstraint(name = "uc_file_name", columnNames = "file_name"),
})
@SequenceGenerator(name = "persons_seq", sequenceName = "seq_persons", allocationSize = 1)
@Data
@NoArgsConstructor
public class PersonRecord implements Persistable<Long> {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "persons_seq")
    private Long id;

    @Column(name = "first_name", nullable = false)
    @NonNull
    private String firstName;

    @Column(name = "last_name", nullable = false)
    @NonNull
    private String lastName;

    @Column(name = "file_name", unique = true, nullable = false)
    @Nullable
    private String fileName;

    @Column(name = "email_address", unique = true)
    @Nullable
    private String emailAddress;

    @OneToOne(cascade = CascadeType.REMOVE, mappedBy = "person", orphanRemoval = true)
    @Nullable
    private MusicianRecord musician;

    @CreationTimestamp
    @Column(name = "created_date_time", nullable = false, updatable = false)
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private Instant createdDate;

    @UpdateTimestamp
    @Column(name = "last_modified_date_time", nullable = false)
    @Setter(AccessLevel.PROTECTED)
    @Nullable
    private Instant lastModifiedDate;

    @Transient
    private boolean isNew;

    public PersonRecord(@NonNull Long id, @NonNull String firstName, @NonNull String lastName) {
        this(firstName, lastName);
        this.id = id;
        this.isNew = false;
    }

    public PersonRecord(@NonNull String firstName, @NonNull String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.isNew = true;
    }

    public Optional<Instant> getCreatedDate() {
        return Optional.ofNullable(createdDate);
    }

    public Optional<Instant> getLastModifiedDate() {
        return Optional.ofNullable(lastModifiedDate);
    }

    @PostPersist
    protected void onPersist() {
        this.isNew = false;
    }

    // @checkstyle:off: NeedBraces
    @Override
    public boolean equals(Object obj) {
        if (null == obj) return false;
        if (this == obj) return true;
        if (!getClass().equals(ProxyUtils.getUserClass(obj))) return false;
        PersonRecord that = (PersonRecord) obj;
        return this.getId() != null && this.getId().equals(that.getId());
    }
    // @checkstyle:on: NeedBraces

    @Override
    public int hashCode() {
        int hashCode = 17;
        hashCode += null == getId() ? 0 : getId().hashCode() * 31;
        return hashCode;
    }

    @Override
    public String toString() {
        Class<?> userClass = ProxyUtils.getUserClass(this);
        return userClass.getSimpleName() + "(id=" + getId() + ")";
    }
}
