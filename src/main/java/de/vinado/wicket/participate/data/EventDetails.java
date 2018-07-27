package de.vinado.wicket.participate.data;

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
import java.io.Serializable;
import java.util.Date;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
@Entity
@Table(name = "v_event_details")
public class EventDetails implements Identifiable, Serializable {

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

    @Column(name = "count_dinner")
    private Long dinnerCount;

    @Column(name = "count_place_to_sleep")
    private Long placeToSleepCount;

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

    @Column(name = "member_soprano")
    private String sopranosAccepted;

    @Column(name = "member_alto")
    private String altosAccepted;

    @Column(name = "member_tenor")
    private String tenorsAccepted;

    @Column(name = "member_bass")
    private String bassesAccepted;

    @Column(name = "member_declined")
    private String declinedMembers;

    @Column(name = "count_member")
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

    public Long getDinnerCount() {
        return dinnerCount;
    }

    public Long getPlaceToSleepCount() {
        return placeToSleepCount;
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

    public String getSopranosAccepted() {
        return sopranosAccepted;
    }

    public String getAltosAccepted() {
        return altosAccepted;
    }

    public String getTenorsAccepted() {
        return tenorsAccepted;
    }

    public String getBassesAccepted() {
        return bassesAccepted;
    }

    public String getDeclinedMembers() {
        return declinedMembers;
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
            .append(dinnerCount, that.dinnerCount)
            .append(placeToSleepCount, that.placeToSleepCount)
            .append(acceptedCount, that.acceptedCount)
            .append(declinedCount, that.declinedCount)
            .append(pendingCount, that.pendingCount)
            .append(sopranoCount, that.sopranoCount)
            .append(altoCount, that.altoCount)
            .append(tenorCount, that.tenorCount)
            .append(bassCount, that.bassCount)
            .append(sopranosAccepted, that.sopranosAccepted)
            .append(altosAccepted, that.altosAccepted)
            .append(tenorsAccepted, that.tenorsAccepted)
            .append(bassesAccepted, that.bassesAccepted)
            .append(declinedMembers, that.declinedMembers)
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
            .append(dinnerCount)
            .append(placeToSleepCount)
            .append(acceptedCount)
            .append(declinedCount)
            .append(pendingCount)
            .append(sopranoCount)
            .append(altoCount)
            .append(tenorCount)
            .append(bassCount)
            .append(sopranosAccepted)
            .append(altosAccepted)
            .append(tenorsAccepted)
            .append(bassesAccepted)
            .append(declinedMembers)
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
            .append("dinnerCount", dinnerCount)
            .append("placeToSleepCount", placeToSleepCount)
            .append("acceptedCount", acceptedCount)
            .append("declinedCount", declinedCount)
            .append("pendingCount", pendingCount)
            .append("sopranoCount", sopranoCount)
            .append("altoCount", altoCount)
            .append("tenorCount", tenorCount)
            .append("bassCount", bassCount)
            .append("sopranosAccepted", sopranosAccepted)
            .append("altosAccepted", altosAccepted)
            .append("tenorsAccepted", tenorsAccepted)
            .append("bassesAccepted", bassesAccepted)
            .append("declinedMembers", declinedMembers)
            .append("totalInvitationCount", totalInvitationCount)
            .toString();
    }
}
