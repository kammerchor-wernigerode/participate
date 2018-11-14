package de.vinado.wicket.participate.model.filters;

import de.vinado.wicket.participate.model.EventDetails;
import lombok.Getter;
import lombok.Setter;
import org.apache.wicket.util.string.Strings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
@Getter
@Setter
public class EventDetailsFilter implements Serializable, IFilter<EventDetails> {

    private String name;
    private Date startDate;
    private Date endDate;
    private String event;
    private String location;

    @Override
    public List<EventDetails> filter(final List<EventDetails> list) {
        final List<EventDetails> result = new ArrayList<>();
        for (EventDetails event : list) {
            if (validate(event.getName(), name))
                continue;

            if (null != startDate && event.getStartDate().before(startDate))
                continue;

            if (null != endDate && event.getEndDate().after(endDate))
                continue;

            if (validate(event.getEventType(), this.event))
                continue;

            if (validate(event.getLocation(), location))
                continue;

            result.add(event);
        }

        return result;
    }

    @Override
    public boolean validate(final String str1, final String str2) {
        return !Strings.isEmpty(str2) && !str1.toLowerCase().contains(str2.toLowerCase());
    }
}
