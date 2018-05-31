package de.vinado.wicket.participate.event;

import org.apache.wicket.ajax.AjaxRequestTarget;

import java.io.Serializable;

/**
 * Event for updating the event information.
 *
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class MemberUpdateEvent implements Serializable {

    private AjaxRequestTarget target;

    /**
     * @param target Requested target
     */
    public MemberUpdateEvent(final AjaxRequestTarget target) {
        this.target = target;
    }

    public AjaxRequestTarget getTarget() {
        return target;
    }
}
