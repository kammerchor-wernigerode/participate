package de.vinado.wicket.participate.data;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
@Entity
@Table(name = "user_rec_tokens")
public class UserRecoveryToken implements Identifiable, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "token", nullable = false)
    private String token;

    @CreationTimestamp
    private Date creationDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "valid_date", nullable = false)
    private Date validDate;


    /**
     * Hibernate only
     */
    protected UserRecoveryToken() {
    }

    /**
     * @param user      {@link User}
     * @param token     Recovery token
     * @param validDate Date, the token expires
     */
    public UserRecoveryToken(final User user, final String token, final Date validDate) {
        this.user = user;
        this.token = token;
        this.creationDate = new Date();
        this.validDate = validDate;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(final User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(final String token) {
        this.token = token;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public Date getValidDate() {
        return validDate;
    }

    public void setValidDate(final Date validDate) {
        this.validDate = validDate;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (!(o instanceof UserRecoveryToken)) return false;

        final UserRecoveryToken that = (UserRecoveryToken) o;

        return new EqualsBuilder()
                .append(getId(), that.getId())
                .append(getUser(), that.getUser())
                .append(getToken(), that.getToken())
                .append(getCreationDate(), that.getCreationDate())
                .append(getValidDate(), that.getValidDate())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getId())
                .append(getUser())
                .append(getToken())
                .append(getCreationDate())
                .append(getValidDate())
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("user", user)
                .append("token", token)
                .append("creationDate", creationDate)
                .append("validDate", validDate)
                .toString();
    }
}
