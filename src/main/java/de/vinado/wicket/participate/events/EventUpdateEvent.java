package de.vinado.wicket.participate.events;

import de.vinado.wicket.participate.model.Event;
import org.apache.wicket.ajax.AjaxRequestTarget;

import java.io.Serializable;

/**
 * Event for updating the event information.
 *
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class EventUpdateEvent implements Serializable {

    /**
     * {@link Event}
     */
    private Event event;

    /**
     * {@link AjaxRequestTarget}
     */
    private AjaxRequestTarget target;

    /**
     * @param event  Event
     * @param target Requested target
     */
    public EventUpdateEvent(final Event event, final AjaxRequestTarget target) {
        this.event = event;
        this.target = target;
    }

    public Event getEvent() {
        return event;
    }

    public AjaxRequestTarget getTarget() {
        return target;
    }
}
