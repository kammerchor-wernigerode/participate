package de.vinado.wicket.participate.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.format.ISODateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
@Entity
@Table(name = "v_event_details")
@Getter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class EventDetails implements Identifiable<Long>, Terminable, Hideable {

    @Id
    @ManyToOne
    @JoinColumn(name = "id")
    private Event event;

    @Column
    private String name;

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

    @Column(name = "count_accepted_declined_pending")
    private String countAcceptedDeclinedPending;

    @Column(name = "count_catering", length = 23, columnDefinition = "DECIMAL")
    private Long cateringCount;

    @Column(name = "count_accommodation", length = 23, columnDefinition = "DECIMAL")
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

    @Column(name = "soprano", columnDefinition = "TEXT")
    private String sopranos;

    @Column(name = "alto", columnDefinition = "TEXT")
    private String altos;

    @Column(name = "tenor", columnDefinition = "TEXT")
    private String tenors;

    @Column(name = "bass", columnDefinition = "TEXT")
    private String basses;

    @Column(columnDefinition = "TEXT")
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
        return ISODateTimeFormat.dateTime().print(creationDate.getTime());
    }

    @Transient
    public long getOffset() {
        long diff = new Date().getTime() - getCreationDate().getTime();
        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }
}
