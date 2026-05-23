package de.kammerchorwernigerode.app.participate.user.infrastructure.jpa;

import de.kammerchorwernigerode.app.participate.person.infrastructure.PersonRecord;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Persistable;
import org.springframework.data.util.ProxyUtils;

import java.util.LinkedHashSet;
import java.util.Set;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PostPersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(name = "uc_users_email_address", columnNames = "email_address"),
})
@SequenceGenerator(name = "users_seq", sequenceName = "seq_users", allocationSize = 1)
@Data
@NoArgsConstructor
public class UserRecord implements Persistable<Long> {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_seq")
    private Long id;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @NonNull
    private Set<UserAccountRecord> accounts = new LinkedHashSet<>();

    @OneToOne(mappedBy = "user")
    @Nullable
    private PersonRecord person;

    @Column(name = "name", nullable = false)
    @NonNull
    private String name;

    @Column(name = "email_address", unique = true)
    @Nullable
    private String emailAddress;

    @Column(name = "is_email_verified", nullable = false)
    private boolean emailVerified;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted;

    @Column(name = "is_locked", nullable = false)
    private boolean locked;

    @Column(name = "is_disabled", nullable = false)
    private boolean disabled;

    @Transient
    private boolean isNew;

    public UserRecord(@NonNull Long id, @NonNull String name) {
        this.id = id;
        this.name = name;
        this.isNew = false;
    }

    public UserRecord(@NonNull String name) {
        this.name = name;
        this.isNew = true;
    }

    @PostPersist
    protected void postPersist() {
        this.isNew = false;
    }

    // @checkstyle:off: NeedBraces
    @Override
    public boolean equals(Object obj) {
        if (null == obj) return false;
        if (this == obj) return true;
        if (!getClass().equals(ProxyUtils.getUserClass(obj))) return false;
        UserRecord that = (UserRecord) obj;
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
