package de.vinado.wicket.participate.events;

import org.apache.wicket.ajax.AjaxRequestTarget;

import java.io.Serializable;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class AjaxUpdateEvent implements Serializable {

    private AjaxRequestTarget target;

    public AjaxUpdateEvent(final AjaxRequestTarget target) {
        this.target = target;
    }

    public AjaxRequestTarget getTarget() {
        return target;
    }
}
