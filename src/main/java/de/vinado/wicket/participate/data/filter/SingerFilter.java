package de.vinado.wicket.participate.data.filter;

import de.vinado.wicket.participate.data.Voice;

import java.io.Serializable;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class SingerFilter implements Serializable {

    private String searchTerm;

    private Voice voice;

    private boolean showAll;

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(final String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public Voice getVoice() {
        return voice;
    }

    public void setVoice(final Voice voice) {
        this.voice = voice;
    }

    public boolean isShowAll() {
        return showAll;
    }

    public void setShowAll(final boolean showAll) {
        this.showAll = showAll;
    }
}
