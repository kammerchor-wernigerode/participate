package de.vinado.wicket.participate.ui.event.details;

import de.vinado.wicket.participate.model.filters.DetailedParticipantFilter;
import lombok.Value;

/**
 * @author Vincent Nadoll
 */
@Value
public class ParticipantFilterIntent {

    DetailedParticipantFilter filter;
}
