package de.vinado.wicket.participate.model;

import de.vinado.app.participate.event.model.Interval;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.time.DateUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

@Entity
@Table(name = "v_event_details")
@Getter
@Setter(AccessLevel.PACKAGE)
@NoArgsConstructor
@ToString
public class EventDetails implements Identifiable<Long>, Terminable, Hideable {

    @Id
    @ManyToOne
    @JoinColumn(name = "id")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Event event;

    @Column(name = "type")
    private String eventType;

    @Column(length = 65535, columnDefinition = "LONGTEXT")
    private String description;

    @Column
    @Temporal(TemporalType.DATE)
    private Date startDate;

    @Column
    @Temporal(TemporalType.DATE)
    private Date endDate;

    @Column
    private String location;

    @OneToMany(
        mappedBy = "eventDetails",
        fetch = FetchType.EAGER
    )
    private List<Participant> participants;

    @Column(name = "count_accepted_declined_pending")
    private String countAcceptedDeclinedPending;

    @Column(name = "count_car", length = 21, columnDefinition = "DECIMAL")
    private Long carCount;

    @Column(name = "count_car_seat", length = 21, columnDefinition = "DECIMAL")
    private Long carSeatCount;

    @Column(name = "count_accepted")
    private Long acceptedCount;

    @Column(name = "count_declined")
    private Long declinedCount;

    @Column(name = "count_tentative")
    private Long tentativeCount;

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

    @Column(name = "soprano", columnDefinition = "MEDIUMTEXT")
    private String sopranos;

    @Column(name = "alto", columnDefinition = "MEDIUMTEXT")
    private String altos;

    @Column(name = "tenor", columnDefinition = "MEDIUMTEXT")
    private String tenors;

    @Column(name = "bass", columnDefinition = "MEDIUMTEXT")
    private String basses;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String tentative;

    @Column(columnDefinition = "MEDIUMTEXT")
    private String declined;

    @Column(name = "count_invitations")
    private Long totalInvitationCount;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "creation_date")
    private Date creationDate;

    @Override
    public Long getId() {
        return event.getId();
    }

    public boolean isSeveralDays() {
        return event.isSeveralDays();
    }

    public String getDisplayDate() {
        return event.getDisplayDate();
    }

    public Date getCreationDate() {
        return DateUtils.truncate(creationDate, Calendar.DATE);
    }

    @Transient
    public String getCreationDateTimeIso() {
        Instant instant = Instant.ofEpochMilli(creationDate.getTime());
        return DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(instant.atZone(ZoneId.systemDefault()));
    }

    @Transient
    public long getOffset() {
        long diff = new Date().getTime() - getCreationDate().getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    @Transient
    public long getAcceptedSum() {
        return acceptedCount + tentativeCount;
    }

    @Transient
    public int getAccommodationDemand() {
        return bedsWhere(Accommodation::isSearching);
    }

    @Transient
    public int getAccommodationSupply() {
        return bedsWhere(Accommodation::isOffering);
    }

    private Integer bedsWhere(Predicate<Accommodation> predicate) {
        return participants.stream()
            .filter(Participant::isConsiderable)
            .map(Participant::getAccommodation)
            .filter(Objects::nonNull)
            .filter(predicate)
            .map(Accommodation::getBeds)
            .reduce(0, Integer::sum);
    }

    @Transient
    public Interval getInterval() {
        return event.getInterval();
    }

    @Transient
    public LocalDate getLocalStartDate() {
        return event.getLocalStartDate();
    }

    @Transient
    public LocalDate getLocalEndDate() {
        return event.getLocalEndDate();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        EventDetails that = (EventDetails) obj;
        return event.equals(that.event);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(event.getId());
    }
}
