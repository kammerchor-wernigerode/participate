package de.vinado.wicket.participate.ui.event;

import de.vinado.wicket.participate.model.Event;
import lombok.Value;
import org.springframework.lang.Nullable;

@Value
public class EventSelectedEvent {
    @Nullable
    Event selection;
}
