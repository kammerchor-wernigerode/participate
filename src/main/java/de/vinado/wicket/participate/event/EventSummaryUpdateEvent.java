package de.vinado.wicket.participate.event;

import de.vinado.wicket.participate.data.view.EventDetailsView;
import org.apache.wicket.ajax.AjaxRequestTarget;

import java.io.Serializable;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class EventSummaryUpdateEvent implements Serializable {

    private EventDetailsView eventDetailsView;

    private AjaxRequestTarget target;

    public EventSummaryUpdateEvent(final EventDetailsView eventDetailsView, final AjaxRequestTarget target) {
        this.eventDetailsView = eventDetailsView;
        this.target = target;
    }

    public EventDetailsView getEventDetailsView() {
        return eventDetailsView;
    }

    public void setEventDetailsView(final EventDetailsView eventDetailsView) {
        this.eventDetailsView = eventDetailsView;
    }

    public AjaxRequestTarget getTarget() {
        return target;
    }

    public void setTarget(final AjaxRequestTarget target) {
        this.target = target;
    }
}
