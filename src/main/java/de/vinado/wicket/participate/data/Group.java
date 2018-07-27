package de.vinado.wicket.participate.data;

import de.vinado.wicket.participate.common.ParticipateUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.UpdateTimestamp;

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
@Table(name = "groups")
public class Group implements Identifiable, Serializable {

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

    @CreationTimestamp
    private Date creationDate;

    @UpdateTimestamp
    private Date lastModified;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "valid_until")
    private Date validUntil;

    @Column(name = "is_editable", nullable = false)
    private boolean editable;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Formula("CONCAT(name, COALESCE(CONCAT(' - ', DATE_FORMAT(valid_until, '%d.%m.%Y')), ''))")
    private String title;

    public Group() {
    }

    public Group(final String name, final String description, final Date validUntil) {
        this.identifier = ParticipateUtils.getGenericIdentifier(name);
        this.name = name;
        this.description = description;
        this.validUntil = validUntil;
        this.creationDate = new Date();
        this.lastModified = new Date();
        this.editable = true;
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

    public Date getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(final Date validUntil) {
        this.validUntil = validUntil;
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

    public String getTitle() {
        return title;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (!(o instanceof Group)) return false;

        final Group group = (Group) o;

        return new EqualsBuilder()
                .append(editable, group.editable)
                .append(identifier, group.identifier)
                .append(active, group.active)
                .append(id, group.id)
                .append(description, group.description)
                .append(name, group.name)
                .append(creationDate, group.creationDate)
                .append(lastModified, group.lastModified)
                .append(validUntil, group.validUntil)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(identifier)
                .append(name)
                .append(description)
                .append(creationDate)
                .append(lastModified)
                .append(validUntil)
                .append(editable)
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
                .append("creationDate", creationDate)
                .append("lastModified", lastModified)
                .append("validUntil", validUntil)
                .append("editable", editable)
                .append("active", active)
                .toString();
    }
}
