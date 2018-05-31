package de.vinado.wicket.participate.data.permission;

import de.vinado.wicket.participate.data.Identifiable;
import de.vinado.wicket.participate.data.ListOfValue;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
@Entity
@Table(name = "m_role_permission")
public class RoleToPermission implements Identifiable, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "permission_id", nullable = false)
    private ListOfValue permission;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "creation_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

    protected RoleToPermission() {
    }

    public RoleToPermission(final ListOfValue permission, final Role role) {
        this.role = role;
        this.permission = permission;
        this.creationDate = new Date();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public ListOfValue getPermission() {
        return permission;
    }

    public void setPermission(final ListOfValue permission) {
        this.permission = permission;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(final Role role) {
        this.role = role;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (!(o instanceof RoleToPermission)) return false;

        final RoleToPermission that = (RoleToPermission) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(permission, that.permission)
                .append(role, that.role)
                .append(creationDate, that.creationDate)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(permission)
                .append(role)
                .append(creationDate)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("permission", permission)
                .append("role", role)
                .append("creationDate", creationDate)
                .toString();
    }
}
