package de.kammerchorwernigerode.app.participate.musician.infrastructure;

import de.kammerchorwernigerode.app.participate.person.infrastructure.PersonRecord;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Persistable;
import org.springframework.data.util.ProxyUtils;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PostPersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "musicians", uniqueConstraints = {
    @UniqueConstraint(name = "uc_person_id", columnNames = "person_id"),
})
@SequenceGenerator(name = "musicians_seq", sequenceName = "seq_musicians", allocationSize = 1)
@Data
@NoArgsConstructor
public class MusicianRecord implements Persistable<Long> {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "musicians_seq")
    private Long id;

    @Column(name = "person_id", nullable = false, updatable = false)
    @NonNull
    private Long personId;

    @MapsId("personId")
    @OneToOne(optional = false)
    @JoinColumn(name = "person_id", referencedColumnName = "id", unique = true, nullable = false, updatable = false,
        foreignKey = @ForeignKey(name = "fk_musicians_person_id"))
    @NonNull
    private PersonRecord person;

    @Enumerated(EnumType.STRING)
    @Column(name = "voice")
    @Nullable
    private Voice voice;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted;

    @Transient
    private boolean isNew;

    public MusicianRecord(@NonNull Long id, @NonNull PersonRecord person) {
        this(person);
        this.id = id;
        this.isNew = false;
    }

    public MusicianRecord(@NonNull PersonRecord person) {
        this.personId = person.getId();
        this.person = person;
        this.isNew = true;
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
        MusicianRecord that = (MusicianRecord) obj;
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
