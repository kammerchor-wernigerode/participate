package de.vinado.wicket.participate.model.dtos;

import de.vinado.wicket.participate.model.Event;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@RequiredArgsConstructor
public class EventDTO implements Serializable {

    private Event event;
    private String description;
    private String eventType;
    private Date startDate;
    private Date endDate;
    private String location;

    public EventDTO(Event event) {
        this.event = event;
        this.description = event.getDescription();
        this.eventType = event.getEventType();
        this.startDate = event.getStartDate();
        this.endDate = event.getEndDate();
        this.location = event.getLocation();
    }

    public boolean isSeveralDays() {
        return !getStartDate().equals(getEndDate());
    }
}
