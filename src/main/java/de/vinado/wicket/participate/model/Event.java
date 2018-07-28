package de.vinado.wicket.participate.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Entity of an Event
 *
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 * @see de.vinado.wicket.participate.services.EventService
 */
@Entity
@Table(name = "events")
public class Event implements Identifiable<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String eventType;

    @Column
    private String location;

    @Column
    private String description;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date endDate;

    @CreationTimestamp
    private Date creationDate;

    @UpdateTimestamp
    private Date lastModified;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    protected Event() {
    } // JPA only

    /**
     * @param name        Event name
     * @param eventType   Event type
     * @param location    Event location
     * @param description More information
     * @param startDate   Start date of Event
     * @param endDate     End date of Event
     */
    public Event(final String name, final String eventType, final String location, final String description,
                 final Date startDate, final Date endDate) {
        this.name = name;
        this.eventType = eventType;
        this.location = location;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.active = true;
    }

    @Override
    public Long getId() {
        return id;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(final String location) {
        this.location = location;
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
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (!(o instanceof Event)) return false;

        final Event event = (Event) o;

        return new EqualsBuilder()
            .append(active, event.active)
            .append(id, event.id)
            .append(name, event.name)
            .append(eventType, event.eventType)
            .append(location, event.location)
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
            .append(name)
            .append(eventType)
            .append(location)
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
            .append("name", name)
            .append("eventType", eventType)
            .append("location", location)
            .append("description", description)
            .append("startDate", startDate)
            .append("endDate", endDate)
            .append("creationDate", creationDate)
            .append("lastModified", lastModified)
            .append("active", active)
            .toString();
    }
}
