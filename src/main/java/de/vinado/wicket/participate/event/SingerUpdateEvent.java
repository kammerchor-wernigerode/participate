package de.vinado.wicket.participate.event;

import org.apache.wicket.ajax.AjaxRequestTarget;

import java.io.Serializable;

/**
 * Event for updating the event information.
 *
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class SingerUpdateEvent implements Serializable {

    private AjaxRequestTarget target;

    /**
     * @param target Requested target
     */
    public SingerUpdateEvent(final AjaxRequestTarget target) {
        this.target = target;
    }

    public AjaxRequestTarget getTarget() {
        return target;
    }
}
