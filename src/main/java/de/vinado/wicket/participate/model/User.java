package de.vinado.wicket.participate.model;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * User
 *
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
@Entity
@Table(name = "users")
public class User implements Identifiable, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
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
     * Hibernate only
     */
    protected User() {
    }

    /**
     * @param username      Username
     * @param plainPassword Password in plaintext
     * @param admin         Flag, if user is admin
     * @param enabled       Flag, if user is enabled
     */
    public User(final String username, final String plainPassword, final boolean admin, final boolean enabled) {
        this.username = username;
        this.passwordSha256 = null != plainPassword ? DigestUtils.sha256Hex(plainPassword) : null;
        this.admin = admin;
        this.enabled = enabled;
        this.active = true;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getPasswordSha256() {
        return passwordSha256;
    }

    public void setPasswordSha256(final String passwordSha256) {
        this.passwordSha256 = passwordSha256;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(final boolean admin) {
        this.admin = admin;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(final Person person) {
        this.person = person;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (!(o instanceof User)) return false;

        final User user = (User) o;

        return new EqualsBuilder()
                .append(admin, user.admin)
                .append(enabled, user.enabled)
                .append(active, user.active)
                .append(id, user.id)
                .append(username, user.username)
                .append(passwordSha256, user.passwordSha256)
                .append(person, user.person)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(username)
                .append(passwordSha256)
                .append(admin)
                .append(enabled)
                .append(active)
                .append(person)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("username", username)
                .append("passwordSha256", passwordSha256)
                .append("admin", admin)
                .append("enabled", enabled)
                .append("active", active)
                .append("person", person)
                .toString();
    }
}
