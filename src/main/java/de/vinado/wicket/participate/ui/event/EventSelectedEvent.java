package de.vinado.wicket.participate.ui.event;

import de.vinado.wicket.participate.model.Event;
import lombok.Value;

/**
 * @author Vincent Nadoll
 */
@Value
public class EventSelectedEvent {
    Event selection;
}
