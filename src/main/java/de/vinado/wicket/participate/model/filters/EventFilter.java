package de.vinado.wicket.participate.model.filters;

import de.vinado.wicket.participate.model.EventDetails;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.danekja.java.util.function.serializable.SerializablePredicate;

import java.util.Date;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
@Getter
@Setter
public class EventFilter implements SerializablePredicate<EventDetails> {

    private String searchTerm;
    private Date startDate;
    private Date endDate;
    private boolean showAll;

    @Override
    public boolean test(EventDetails event) {
        return matchesSearchTerm(event)
            && greaterThenEqualsStartDate(event)
            && lessThenEqualsEndDate(event);
    }

    private boolean matchesSearchTerm(EventDetails event) {
        return matchesName(event)
            || matchesType(event)
            || matchesLocation(event);
    }

    private boolean matchesName(EventDetails event) {
        return null == searchTerm || StringUtils.containsIgnoreCase(event.getName(), searchTerm);
    }

    private boolean matchesType(EventDetails event) {
        return null == searchTerm || StringUtils.containsIgnoreCase(event.getEventType(), searchTerm);
    }

    private boolean matchesLocation(EventDetails event) {
        return null == searchTerm || StringUtils.containsIgnoreCase(event.getLocation(), searchTerm);
    }

    private boolean greaterThenEqualsStartDate(EventDetails event) {
        if (null == this.startDate) return true;
        Date startDate = event.getStartDate();
        return this.startDate.before(startDate) || this.startDate.equals(startDate);
    }

    private boolean lessThenEqualsEndDate(EventDetails event) {
        if (null == this.endDate) return true;
        Date endDate = event.getEndDate();
        return this.endDate.after(endDate) || this.endDate.equals(endDate);
    }
}
