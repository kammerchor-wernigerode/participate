package de.vinado.wicket.participate.events;

import de.vinado.wicket.participate.model.Event;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.wicket.ajax.AjaxRequestTarget;

import java.io.Serializable;

/**
 * Event for updating the event information.
 *
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
@Getter
@RequiredArgsConstructor
public class EventUpdateEvent implements Serializable {

    private final Event event;
    private final AjaxRequestTarget target;
}
