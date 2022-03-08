package de.vinado.wicket.participate.ui.event;

import de.vinado.wicket.participate.model.filters.EventFilter;
import lombok.Value;

@Value
public class EventFilterIntent {
    EventFilter filter;
}
