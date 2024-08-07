package de.vinado.wicket.participate.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.security.core.AuthenticatedPrincipal;

import java.util.Objects;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class User implements Identifiable<Long>, Hideable, AuthenticatedPrincipal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(name = "pwd_sha256")
    private String passwordSha256;

    @Column(name = "is_admin", nullable = false)
    private boolean admin;

    @Column(name = "is_enabled", nullable = false)
    private boolean enabled;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @ManyToOne
    @JoinColumn(name = "person_id")
    private Person person;

    public User(String username, String plainPassword, boolean admin, boolean enabled) {
        this.username = username;
        this.passwordSha256 = null != plainPassword ? DigestUtils.sha256Hex(plainPassword) : null;
        this.admin = admin;
        this.enabled = enabled;
        this.active = true;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return 7;
    }

    @Transient
    @Override
    public String getName() {
        return username;
    }
}
