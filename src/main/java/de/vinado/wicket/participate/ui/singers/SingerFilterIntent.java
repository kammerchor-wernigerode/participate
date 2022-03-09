package de.vinado.wicket.participate.ui.singers;

import de.vinado.wicket.participate.model.filters.SingerFilter;
import lombok.Value;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
@Value
public class SingerFilterIntent {
    SingerFilter filter;
}
