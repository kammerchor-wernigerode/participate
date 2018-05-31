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
@Table(name = "communication")
public class Communication implements Identifiable, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "value", nullable = false)
    private String value;

    @ManyToOne
    @JoinColumn(name = "communication_type_id", nullable = false)
    private ListOfValue communicationType;

    protected Communication() {
    }

    public Communication(final String value, final ListOfValue communicationType) {
        this.value = value;
        this.communicationType = communicationType;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public ListOfValue getCommunicationType() {
        return communicationType;
    }

    public void setCommunicationType(final ListOfValue communicationType) {
        this.communicationType = communicationType;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (!(o instanceof Communication)) return false;

        final Communication that = (Communication) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(value, that.value)
                .append(communicationType, that.communicationType)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(value)
                .append(communicationType)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("value", value)
                .append("communicationType", communicationType)
                .toString();
    }


}
