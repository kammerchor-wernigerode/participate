package de.vinado.wicket.participate.events;

import de.vinado.wicket.participate.data.EventDetails;
import org.apache.wicket.ajax.AjaxRequestTarget;

import java.io.Serializable;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class EventSummaryUpdateEvent implements Serializable {

    private EventDetails eventDetails;

    private AjaxRequestTarget target;

    public EventSummaryUpdateEvent(final EventDetails eventDetails, final AjaxRequestTarget target) {
        this.eventDetails = eventDetails;
        this.target = target;
    }

    public EventDetails getEventDetails() {
        return eventDetails;
    }

    public void setEventDetails(final EventDetails eventDetails) {
        this.eventDetails = eventDetails;
    }

    public AjaxRequestTarget getTarget() {
        return target;
    }

    public void setTarget(final AjaxRequestTarget target) {
        this.target = target;
    }
}
