package de.vinado.wicket.participate.model.filters;

import de.vinado.wicket.participate.ui.event.SelectableEventDetails;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.danekja.java.util.function.serializable.SerializablePredicate;

import java.util.Date;

@Getter
@Setter
public class EventFilter implements SerializablePredicate<SelectableEventDetails> {

    private String searchTerm;
    private Date startDate;
    private Date endDate;
    private boolean showAll;

    @Override
    public boolean test(SelectableEventDetails event) {
        return matchesSearchTerm(event)
            && greaterThenEqualsStartDate(event)
            && lessThenEqualsEndDate(event);
    }

    private boolean matchesSearchTerm(SelectableEventDetails event) {
        return matchesName(event)
            || matchesType(event)
            || matchesLocation(event);
    }

    private boolean matchesName(SelectableEventDetails event) {
        return null == searchTerm || StringUtils.containsIgnoreCase(event.getName(), searchTerm);
    }

    private boolean matchesType(SelectableEventDetails event) {
        return null == searchTerm || StringUtils.containsIgnoreCase(event.getEventType(), searchTerm);
    }

    private boolean matchesLocation(SelectableEventDetails event) {
        return null == searchTerm || StringUtils.containsIgnoreCase(event.getLocation(), searchTerm);
    }

    private boolean greaterThenEqualsStartDate(SelectableEventDetails event) {
        if (null == this.startDate) return true;
        Date startDate = event.getStartDate();
        return this.startDate.before(startDate) || this.startDate.equals(startDate);
    }

    private boolean lessThenEqualsEndDate(SelectableEventDetails event) {
        if (null == this.endDate) return true;
        Date endDate = event.getEndDate();
        return this.endDate.after(endDate) || this.endDate.equals(endDate);
    }
}
