package de.vinado.wicket.participate.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Entity
@Table(name = "events")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Event implements Identifiable<Long>, Hideable, Terminable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String eventType;

    @Column
    private String location;

    @Column(length = 65535, columnDefinition = "LONGTEXT")
    private String description;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date startDate;

    @Temporal(TemporalType.DATE)
    @Column(nullable = false)
    private Date endDate;

    @CreationTimestamp
    @Setter(AccessLevel.NONE)
    private Date creationDate;

    @UpdateTimestamp
    @Setter(AccessLevel.NONE)
    private Date lastModified;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    public Event(String name, String eventType, String location, String description,
                 Date startDate, Date endDate) {
        this.name = name;
        this.eventType = eventType;
        this.location = location;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.active = true;
    }

    public boolean isSeveralDays() {
        return !getStartDate().equals(getEndDate());
    }

    public String getDisplayDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        if (isSeveralDays()) {
            return dateFormat.format(startDate) + " - " + dateFormat.format(endDate);
        } else {
            return dateFormat.format(startDate);
        }
    }

    @Transient
    public boolean isUpcoming() {
        Date now = new Date();
        return now.before(startDate);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Event that = (Event) obj;
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return 13;
    }
}
