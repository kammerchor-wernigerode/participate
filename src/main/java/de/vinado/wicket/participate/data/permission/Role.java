package de.vinado.wicket.participate.data.permission;

import de.vinado.wicket.participate.data.Identifiable;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
@Entity
@Table(name = "roles")
public class Role implements Identifiable, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "identifier", nullable = false, unique = true)
    private String identifier;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "is_editable", nullable = false)
    private boolean editable;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creation_date", nullable = false, updatable = false)
    private Date creationDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_modified", nullable = false)
    private Date lastModified;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    /**
     * Hibernate only
     */
    protected Role() {
    }

    public Role(final String identifier, final String name, final String description) {
        this.identifier = identifier;
        this.name = name;
        this.description = description;
        this.editable = true;
        this.creationDate = new Date();
        this.lastModified = new Date();
        this.active = true;
    }

    @PreUpdate
    private void onUpdate() {
        this.lastModified = new Date();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(final boolean editable) {
        this.editable = editable;
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

        if (!(o instanceof Role)) return false;

        final Role role = (Role) o;

        return new EqualsBuilder()
                .append(editable, role.editable)
                .append(active, role.active)
                .append(id, role.id)
                .append(identifier, role.identifier)
                .append(name, role.name)
                .append(description, role.description)
                .append(creationDate, role.creationDate)
                .append(lastModified, role.lastModified)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(identifier)
                .append(name)
                .append(description)
                .append(editable)
                .append(creationDate)
                .append(lastModified)
                .append(active)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("identifier", identifier)
                .append("name", name)
                .append("description", description)
                .append("editable", editable)
                .append("creationDate", creationDate)
                .append("lastModified", lastModified)
                .append("active", active)
                .toString();
    }
}
