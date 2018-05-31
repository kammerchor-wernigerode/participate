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
@Table(name = "attribute")
public class Attribute implements Identifiable, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "value", nullable = false)
    private String value;

    @ManyToOne
    @JoinColumn(name = "attribute_type_id", nullable = false)
    private AttributeType attributeType;

    public Attribute() {
    }

    public Attribute(final String value, final AttributeType attributeType) {
        this.value = value;
        this.attributeType = attributeType;
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

    public AttributeType getAttributeType() {
        return attributeType;
    }

    public void setAttributeType(final AttributeType attributeType) {
        this.attributeType = attributeType;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (!(o instanceof Attribute)) return false;

        final Attribute attribute = (Attribute) o;

        return new EqualsBuilder()
                .append(id, attribute.id)
                .append(value, attribute.value)
                .append(attributeType, attribute.attributeType)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(value)
                .append(attributeType)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("value", value)
                .append("attributeType", attributeType)
                .toString();
    }
}
