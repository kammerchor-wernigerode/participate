package de.vinado.wicket.participate.data;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
@Entity
@Table(name = "m_group_event")
public class GroupToEvent implements Identifiable, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false, unique = true)
    private Event event;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    public GroupToEvent() {
    }

    public GroupToEvent(final Event event, final Group group) {
        this.event = event;
        this.group = group;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(final Event event) {
        this.event = event;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(final Group group) {
        this.group = group;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (!(o instanceof GroupToEvent)) return false;

        final GroupToEvent that = (GroupToEvent) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(event, that.event)
                .append(group, that.group)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(event)
                .append(group)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("event", event)
                .append("group", group)
                .toString();
    }
}
