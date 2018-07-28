package de.vinado.wicket.participate.events;

import org.apache.wicket.ajax.AjaxRequestTarget;

import java.io.Serializable;

/**
 * Event for updating the event information.
 *
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class RemoveEventUpdateEvent implements Serializable {

    /**
     * {@link org.apache.wicket.ajax.AjaxRequestTarget}
     */
    private AjaxRequestTarget target;

    /**
     * @param target Requested target
     */
    public RemoveEventUpdateEvent(final AjaxRequestTarget target) {
        this.target = target;
    }

    public AjaxRequestTarget getTarget() {
        return target;
    }
}
