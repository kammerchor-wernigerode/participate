package de.vinado.wicket.participate.data.filter;

import de.vinado.wicket.participate.data.EventDetails;
import de.vinado.wicket.participate.data.Group;
import org.apache.wicket.util.string.Strings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class EventDetailsFilter implements Serializable, IFilter<EventDetails> {

    private String name;

    private Date startDate;

    private Date endDate;

    private String event;

    private String location;

    private Group group;

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

            if (null != group && !event.getCast().equalsIgnoreCase(group.getName()))
                continue;

            result.add(event);
        }

        return result;
    }

    @Override
    public boolean validate(final String str1, final String str2) {
        return !Strings.isEmpty(str2) && !str1.toLowerCase().contains(str2.toLowerCase());
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
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

    public String getEvent() {
        return event;
    }

    public void setEvent(final String event) {
        this.event = event;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(final String location) {
        this.location = location;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(final Group group) {
        this.group = group;
    }
}
