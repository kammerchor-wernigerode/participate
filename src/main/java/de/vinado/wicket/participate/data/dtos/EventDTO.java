package de.vinado.wicket.participate.data.dtos;

import de.vinado.wicket.participate.data.Event;

import java.io.Serializable;
import java.util.Date;

/**
 * Event data transfer object
 *
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class EventDTO implements Serializable {

    private Event event;

    private String identifier;

    private String name;

    private String description;

    private String eventType;

    private Date startDate;

    private Date endDate;

    private String location;

    public EventDTO() {
    }

    public EventDTO(final Event event) {
        this.event = event;
        this.description = event.getDescription();
        this.eventType = event.getEventType();
        this.startDate = event.getStartDate();
        this.endDate = event.getEndDate();
        this.location = event.getLocation();
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(final Event event) {
        this.event = event;
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

    public String getEventType() {
        return eventType;
    }

    public void setEventType(final String eventType) {
        this.eventType = eventType;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(final String location) {
        this.location = location;
    }

    public boolean isSeveralDays() {
        return !getStartDate().equals(getEndDate());
    }
}
