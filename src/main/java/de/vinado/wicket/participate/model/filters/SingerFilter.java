package de.vinado.wicket.participate.model.filters;

import de.vinado.wicket.participate.model.Voice;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
@Getter
@Setter
public class SingerFilter implements Serializable {

    private String searchTerm;
    private Voice voice;
    private boolean showAll;
}
