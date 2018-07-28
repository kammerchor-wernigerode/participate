package de.vinado.wicket.participate.events;

import org.apache.wicket.ajax.AjaxRequestTarget;

import java.io.Serializable;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class ShowHidePropertiesEvent implements Serializable {


    private AjaxRequestTarget target;

    public ShowHidePropertiesEvent(final AjaxRequestTarget target) {
        this.target = target;
    }

    public AjaxRequestTarget getTarget() {
        return target;
    }

    public void setTarget(final AjaxRequestTarget target) {
        this.target = target;
    }
}
