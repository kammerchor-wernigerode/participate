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
@Table(name = "m_communication_person")
public class CommunicationToPerson implements Identifiable, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "communication_id", nullable = false)
    private Communication communication;

    @ManyToOne
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    public CommunicationToPerson() {
    }

    public CommunicationToPerson(final Communication communication, final Person person) {
        this.communication = communication;
        this.person = person;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Communication getCommunication() {
        return communication;
    }

    public void setCommunication(final Communication communication) {
        this.communication = communication;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(final Person person) {
        this.person = person;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (!(o instanceof CommunicationToPerson)) return false;

        final CommunicationToPerson that = (CommunicationToPerson) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(communication, that.communication)
                .append(person, that.person)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(communication)
                .append(person)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("communication", communication)
                .append("person", person)
                .toString();
    }
}
