package de.vinado.wicket.participate.data.dto;

import de.vinado.wicket.participate.data.Group;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class GroupDTO implements Serializable {

    private Group group;

    private String name;

    private String description;

    private Date creationDate;

    private Date lastModified;

    private Date validUntil;

    private boolean editable;

    private boolean active;

    public GroupDTO() {
    }

    public GroupDTO(final Group group) {
        this.group = group;
        this.name = group.getName();
        this.description = group.getDescription();
        this.creationDate = group.getCreationDate();
        this.lastModified = group.getLastModified();
        this.validUntil = group.getValidUntil();
        this.editable = group.isEditable();
        this.active = group.isActive();
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(final Group group) {
        this.group = group;
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

    public void setCreationDate(final Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(final Date lastModified) {
        this.lastModified = lastModified;
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
}
