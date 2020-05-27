package de.vinado.wicket.participate.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

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
}
