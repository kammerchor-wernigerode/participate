package de.vinado.wicket.participate.data.view;

import de.vinado.wicket.participate.data.Event;
import de.vinado.wicket.participate.data.Identifiable;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Formula;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
@Entity
@Table(name = "v_events")
public class EventView implements Identifiable, Serializable {

    @Id
    @ManyToOne
    @JoinColumn(name = "id")
    private Event event;

    @Column(name = "name")
    private String name;

    @Column(name = "type")
    private String eventType;

    @Column(name = "description")
    private String description;

    @Column(name = "start_date")
    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Column(name = "end_date")
    @Temporal(TemporalType.DATE)
    private Date endDate;

    @Column(name = "location")
    private String location;

    @Column(name = "count_accepted_declined_pending")
    private String countAcceptedDeclinedPending;

    @Column(name = "cast")
    private String cast;

    @Formula("IF(start_date = end_date, false, true)")
    private boolean severalDays;

    @Override
    public Long getId() {
        return event.getId();
    }

    public Event getEvent() {
        return event;
    }

    public String getName() {
        return name;
    }

    public String getEventType() {
        return eventType;
    }

    public String getDescription() {
        return description;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public String getLocation() {
        return location;
    }

    public String getCountAcceptedDeclinedPending() {
        return countAcceptedDeclinedPending;
    }

    public String getDisplayDate() {
        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        if (severalDays) {
            return simpleDateFormat.format(startDate) + " - " + simpleDateFormat.format(endDate);
        }
        return simpleDateFormat.format(startDate);
    }

    public String getCast() {
        return cast;
    }

    public boolean isSeveralDays() {
        return severalDays;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (!(o instanceof EventView)) return false;

        final EventView eventView = (EventView) o;

        return new EqualsBuilder()
                .append(severalDays, eventView.severalDays)
                .append(event, eventView.event)
                .append(name, eventView.name)
                .append(eventType, eventView.eventType)
                .append(description, eventView.description)
                .append(startDate, eventView.startDate)
                .append(endDate, eventView.endDate)
                .append(location, eventView.location)
                .append(countAcceptedDeclinedPending, eventView.countAcceptedDeclinedPending)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(event)
                .append(name)
                .append(eventType)
                .append(description)
                .append(startDate)
                .append(endDate)
                .append(location)
                .append(countAcceptedDeclinedPending)
                .append(severalDays)
                .toHashCode();
    }
}
