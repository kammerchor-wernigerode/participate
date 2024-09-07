package de.vinado.wicket.participate.ui.event;

import de.vinado.wicket.participate.model.EventDetails;
import lombok.Data;
import lombok.experimental.Delegate;

import java.io.Serializable;

@Data
public class SelectableEventDetails implements Serializable {

    private boolean selected;

    @Delegate
    private final EventDetails subject;
}
