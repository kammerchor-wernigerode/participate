package de.vinado.wicket.participate.data;

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
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Entity of an Event
 *
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 * @see de.vinado.wicket.participate.service.EventService
 */
@Entity
@Table(name = "events")
public class Event implements Identifiable, Serializable, Addressable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "identifier", nullable = false)
    private String identifier;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "description")
    private String description;

    @Temporal(TemporalType.DATE)
    @Column(name = "start_date", nullable = false)
    private Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "end_date", nullable = false)
    private Date endDate;

    @Column(name = "creation_date", nullable = false)
    private Date creationDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_modified")
    private Date lastModified;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    /**
     * Hibernate only
     */
    public Event() {
    }

    /**
     * Construct.
     *
     * @param identifier  Identifier
     * @param name        Event name
     * @param eventType   Event type
     * @param description More information
     * @param startDate   Start date of Event
     * @param endDate     End date of Event
     */
    public Event(final String identifier, final String name, final String eventType, final String description,
                 final Date startDate, final Date endDate) {
        this.identifier = identifier;
        this.name = name;
        this.eventType = eventType;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.creationDate = this.lastModified = new Date();
        this.active = true;
    }

    @PreUpdate
    protected void onUpdate() {
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

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(final String eventType) {
        this.eventType = eventType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(final Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(final Date endDate) {
        this.endDate = endDate;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

    public boolean isSeveralDays() {
        return !getStartDate().equals(getEndDate());
    }

    public String getDisplayDate() {
        final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.YYYY");
        if (isSeveralDays()) {
            return dateFormat.format(startDate) + " - " + dateFormat.format(endDate);
        } else {
            return dateFormat.format(startDate);
        }
    }

    @Override
    public Class getAddressMappingClass() {
        return AddressToEvent.class;
    }

    @Override
    public AddressToEvent addAddressForObject(final Address address) {
        return new AddressToEvent(address, this);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (!(o instanceof Event)) return false;

        final Event event = (Event) o;

        return new EqualsBuilder()
                .append(active, event.active)
                .append(id, event.id)
                .append(identifier, event.identifier)
                .append(name, event.name)
                .append(eventType, event.eventType)
                .append(description, event.description)
                .append(startDate, event.startDate)
                .append(endDate, event.endDate)
                .append(creationDate, event.creationDate)
                .append(lastModified, event.lastModified)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(identifier)
                .append(name)
                .append(eventType)
                .append(description)
                .append(startDate)
                .append(endDate)
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
                .append("eventType", eventType)
                .append("description", description)
                .append("startDate", startDate)
                .append("endDate", endDate)
                .append("creationDate", creationDate)
                .append("lastModified", lastModified)
                .append("active", active)
                .toString();
    }
}
