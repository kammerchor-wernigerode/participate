package de.vinado.wicket.participate.data.filters;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class EventFilter implements Serializable {

    private String searchTerm;

    private Date startDate;

    private Date endDate;

    private boolean showAll;

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(final String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(final Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(final Date endDate) {
        this.endDate = endDate;
    }

    public boolean isShowAll() {
        return showAll;
    }

    public void setShowAll(final boolean showAll) {
        this.showAll = showAll;
    }
}
