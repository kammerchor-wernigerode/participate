package de.vinado.wicket.participate.ui.event.details;

import de.vinado.wicket.participate.model.filters.ParticipantFilter;
import lombok.Value;

/**
 * @author Vincent Nadoll
 */
@Value
public class ParticipantFilterIntent {

    ParticipantFilter filter;
}
