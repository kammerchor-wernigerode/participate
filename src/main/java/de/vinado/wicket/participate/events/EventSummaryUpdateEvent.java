package de.vinado.wicket.participate.events;

import de.vinado.wicket.participate.model.EventDetails;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.wicket.ajax.AjaxRequestTarget;

import java.io.Serializable;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
@Getter
@RequiredArgsConstructor
public class EventSummaryUpdateEvent implements Serializable {

    private final EventDetails eventDetails;
    private final AjaxRequestTarget target;
}
