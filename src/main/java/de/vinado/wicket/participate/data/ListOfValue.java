package de.vinado.wicket.participate.data;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
@Entity
@Inheritance
@Table(name = "c_list_of_value")
public class ListOfValue implements Identifiable, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "dtype", nullable = false, insertable = false, updatable = false, length = 31)
    private String dType;

    @Column(name = "identifier", nullable = false, unique = true)
    private String identifier;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "is_default", nullable = false)
    private boolean isDefault;

    @Column(name = "sort_order", nullable = false)
    private Long sortOrder;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Column(name = "creation_date", nullable = false)
    private Date creationDate;

    protected ListOfValue() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getdType() {
        return dType;
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

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(final boolean aDefault) {
        isDefault = aDefault;
    }

    public Long getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(final Long sortOrder) {
        this.sortOrder = sortOrder;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (!(o instanceof ListOfValue)) return false;

        final ListOfValue that = (ListOfValue) o;

        return new EqualsBuilder()
                .append(isDefault, that.isDefault)
                .append(active, that.active)
                .append(id, that.id)
                .append(dType, that.dType)
                .append(identifier, that.identifier)
                .append(name, that.name)
                .append(description, that.description)
                .append(sortOrder, that.sortOrder)
                .append(creationDate, that.creationDate)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(dType)
                .append(identifier)
                .append(name)
                .append(description)
                .append(isDefault)
                .append(sortOrder)
                .append(active)
                .append(creationDate)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("dType", dType)
                .append("identifier", identifier)
                .append("name", name)
                .append("description", description)
                .append("isDefault", isDefault)
                .append("sortOrder", sortOrder)
                .append("active", active)
                .append("creationDate", creationDate)
                .toString();
    }
}
