package de.vinado.wicket.participate.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.codec.digest.DigestUtils;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * User
 *
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
@NamedQueries({
    @NamedQuery(
        name = "User.findByLogin",
        query = "select u from User u left join u.person p where u.username = :login or p.email = upper(:login)"
    ),
    @NamedQuery(
        name = "User.authenticate",
        query = "select u from User u left join u.person p where (u.username = :login or p.email = upper(:login)) and u.passwordSha256 = :password"
    )
})
@Entity
@Table(name = "users")
@Where(clause = "is_active = true")
@SQLDelete(sql = "update users set is_active = false where id = ?")
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
}
