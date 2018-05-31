package de.vinado.wicket.participate.event;

import de.vinado.wicket.participate.data.Group;
import org.apache.wicket.ajax.AjaxRequestTarget;

import java.io.Serializable;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class GroupUpdateEvent implements Serializable {

    private Group group;

    private AjaxRequestTarget target;

    public GroupUpdateEvent(final AjaxRequestTarget target) {
        this.target = target;
    }

    public GroupUpdateEvent(final Group group, final AjaxRequestTarget target) {
        this.group = group;
        this.target = target;
    }

    public Group getGroup() {
        return group;
    }

    public AjaxRequestTarget getTarget() {
        return target;
    }
}
