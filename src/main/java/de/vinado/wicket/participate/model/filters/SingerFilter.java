package de.vinado.wicket.participate.model.filters;

import de.vinado.wicket.participate.model.Singer;
import de.vinado.wicket.participate.model.Voice;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.function.Predicate;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
@Getter
@Setter
public class SingerFilter implements Predicate<Singer>, Serializable {

    private String searchTerm;
    private Voice voice;
    private boolean showAll;

    @Override
    public boolean test(Singer singer) {
        return matchesSearchTerm(singer)
            && matchesVoice(singer);
    }

    private boolean matchesSearchTerm(Singer singer) {
        return null == searchTerm || StringUtils.containsIgnoreCase(singer.getSearchName(), searchTerm);
    }

    private boolean matchesVoice(Singer singer) {
        return null == voice || voice.equals(singer.getVoice());
    }
}
