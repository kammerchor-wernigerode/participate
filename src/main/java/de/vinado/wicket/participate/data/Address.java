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
 * Address entity
 *
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
@Entity
@Table(name = "addresses")
public class Address implements Identifiable, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "street_address")
    private String streetAddress;

    @Column(name = "postal_code")
    private String postalCode;

    @Column(name = "locality")
    private String locality;

    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;

    public Address() {
    }

    public Address(final String locality) {
        this.locality = locality;
    }

    public Address(final String streetAddress, final String postalCode, final String locality, final Country country) {
        this.streetAddress = streetAddress;
        this.postalCode = postalCode;
        this.locality = locality;
        this.country = country;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(final String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(final String postalCode) {
        this.postalCode = postalCode;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(final String locality) {
        this.locality = locality;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(final Country country) {
        this.country = country;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (!(o instanceof Address)) return false;

        final Address address = (Address) o;

        return new EqualsBuilder()
                .append(id, address.id)
                .append(streetAddress, address.streetAddress)
                .append(postalCode, address.postalCode)
                .append(locality, address.locality)
                .append(country, address.country)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(streetAddress)
                .append(postalCode)
                .append(locality)
                .append(country)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("streetAddress", streetAddress)
                .append("postalCode", postalCode)
                .append("locality", locality)
                .append("country", country)
                .toString();
    }


}
