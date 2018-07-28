package de.vinado.wicket.participate.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
@Entity
@Table(name = "v_event_details")
public class EventDetails implements Identifiable<Long>, Terminable {

    @Id
    @ManyToOne
    @JoinColumn(name = "id")
    private Event event;

    @Column
    private String name;

    @Column(name = "type")
    private String eventType;

    @Column
    private String description;

    @Column
    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Column
    @Temporal(TemporalType.DATE)
    private Date endDate;

    @Column
    private String location;

    @Column(name = "count_accepted_declined_pending")
    private String countAcceptedDeclinedPending;

    @Column(name = "count_catering")
    private Long cateringCount;

    @Column(name = "count_accommodation")
    private Long accommodationCount;

    @Column(name = "count_accepted")
    private Long acceptedCount;

    @Column(name = "count_declined")
    private Long declinedCount;

    @Column(name = "count_pending")
    private Long pendingCount;

    @Column(name = "count_soprano")
    private Long sopranoCount;

    @Column(name = "count_alto")
    private Long altoCount;

    @Column(name = "count_tenor")
    private Long tenorCount;

    @Column(name = "count_bass")
    private Long bassCount;

    @Column(name = "soprano")
    private String sopranos;

    @Column(name = "alto")
    private String altos;

    @Column(name = "tenor")
    private String tenors;

    @Column(name = "bass")
    private String basses;

    @Column
    private String declined;

    @Column(name = "count_invitations")
    private Long totalInvitationCount;

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

    public Long getCateringCount() {
        return cateringCount;
    }

    public Long getAccommodationCount() {
        return accommodationCount;
    }

    public Long getAcceptedCount() {
        return acceptedCount;
    }

    public Long getDeclinedCount() {
        return declinedCount;
    }

    public Long getPendingCount() {
        return pendingCount;
    }

    public Long getSopranoCount() {
        return sopranoCount;
    }

    public Long getAltoCount() {
        return altoCount;
    }

    public Long getTenorCount() {
        return tenorCount;
    }

    public Long getBassCount() {
        return bassCount;
    }

    public String getSopranos() {
        return sopranos;
    }

    public String getAltos() {
        return altos;
    }

    public String getTenors() {
        return tenors;
    }

    public String getBasses() {
        return basses;
    }

    public String getDeclined() {
        return declined;
    }

    public Long getTotalInvitationCount() {
        return totalInvitationCount;
    }

    public boolean isSeveralDays() {
        return event.isSeveralDays();
    }

    public String getDisplayDate() {
        return event.getDisplayDate();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (!(o instanceof EventDetails)) return false;

        final EventDetails that = (EventDetails) o;

        return new EqualsBuilder()
            .append(event, that.event)
            .append(name, that.name)
            .append(eventType, that.eventType)
            .append(description, that.description)
            .append(startDate, that.startDate)
            .append(endDate, that.endDate)
            .append(location, that.location)
            .append(countAcceptedDeclinedPending, that.countAcceptedDeclinedPending)
            .append(cateringCount, that.cateringCount)
            .append(accommodationCount, that.accommodationCount)
            .append(acceptedCount, that.acceptedCount)
            .append(declinedCount, that.declinedCount)
            .append(pendingCount, that.pendingCount)
            .append(sopranoCount, that.sopranoCount)
            .append(altoCount, that.altoCount)
            .append(tenorCount, that.tenorCount)
            .append(bassCount, that.bassCount)
            .append(sopranos, that.sopranos)
            .append(altos, that.altos)
            .append(tenors, that.tenors)
            .append(basses, that.basses)
            .append(declined, that.declined)
            .append(totalInvitationCount, that.totalInvitationCount)
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
            .append(cateringCount)
            .append(accommodationCount)
            .append(acceptedCount)
            .append(declinedCount)
            .append(pendingCount)
            .append(sopranoCount)
            .append(altoCount)
            .append(tenorCount)
            .append(bassCount)
            .append(sopranos)
            .append(altos)
            .append(tenors)
            .append(basses)
            .append(declined)
            .append(totalInvitationCount)
            .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("event", event)
            .append("name", name)
            .append("eventType", eventType)
            .append("description", description)
            .append("startDate", startDate)
            .append("endDate", endDate)
            .append("location", location)
            .append("countAcceptedDeclinedPending", countAcceptedDeclinedPending)
            .append("cateringCount", cateringCount)
            .append("accommodationCount", accommodationCount)
            .append("acceptedCount", acceptedCount)
            .append("declinedCount", declinedCount)
            .append("pendingCount", pendingCount)
            .append("sopranoCount", sopranoCount)
            .append("altoCount", altoCount)
            .append("tenorCount", tenorCount)
            .append("bassCount", bassCount)
            .append("sopranos", sopranos)
            .append("altos", altos)
            .append("tenors", tenors)
            .append("basses", basses)
            .append("declinedSingers", declined)
            .append("totalInvitationCount", totalInvitationCount)
            .toString();
    }
}
