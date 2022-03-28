package de.vinado.wicket.participate.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * User
 *
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class User implements Identifiable<Long>, Hideable {

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

    /**
     * @param username      Username
     * @param plainPassword Password in plaintext
     * @param admin         Whether is administrator
     * @param enabled       Whether is enabled and permitted to use the application
     */
    public User(final String username, final String plainPassword, final boolean admin, final boolean enabled) {
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
}
