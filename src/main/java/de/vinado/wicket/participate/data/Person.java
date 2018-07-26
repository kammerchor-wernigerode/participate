package de.vinado.wicket.participate.data;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Formula;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

/**
 * Person
 *
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
@Entity
@Table(name = "persons")
public class Person implements Identifiable, Serializable, Communicatable, Attributable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "creation_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;

    @Column(name = "last_modified", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModified;

    @Formula("CONCAT(first_name, ' ', last_name)")
    private String displayName;

    @Formula("CONCAT(last_name, ', ', first_name)")
    private String sortName;

    @Formula("CONCAT(first_name, ' ', last_name, ' (', COALESCE(email, 'Email ist nicht hinterlegt'), ')')")
    private String searchName;

    /**
     * Hibernate only
     */
    public Person() {
    }

    public Person(final String firstName, final String lastName, final String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.creationDate = new Date();
        this.lastModified = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        this.lastModified = new Date();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getSortName() {
        return sortName;
    }

    public String getSearchName() {
        return searchName;
    }

    @Override
    public Class getCommunicationMappingClass() {
        return CommunicationToPerson.class;
    }

    @Override
    public Object addCommunicationForObject(final Communication communication) {
        return new CommunicationToPerson(communication, this);
    }

    @Override
    public Class getAttributeMappingClass() {
        return AttributeToPerson.class;
    }

    @Override
    public AttributeToPerson addAttributeForObject(final Attribute attribute) {
        return new AttributeToPerson(attribute, this);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (!(o instanceof Person)) return false;

        final Person person = (Person) o;

        return new EqualsBuilder()
                .append(id, person.id)
                .append(firstName, person.firstName)
                .append(lastName, person.lastName)
                .append(email, person.email)
                .append(creationDate, person.creationDate)
                .append(lastModified, person.lastModified)
                .append(displayName, person.displayName)
                .append(sortName, person.sortName)
                .append(searchName, person.searchName)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(firstName)
                .append(lastName)
                .append(email)
                .append(creationDate)
                .append(lastModified)
                .append(displayName)
                .append(sortName)
                .append(searchName)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("firstName", firstName)
                .append("lastName", lastName)
                .append("email", email)
                .append("creationDate", creationDate)
                .append("lastModified", lastModified)
                .append("displayName", displayName)
                .append("sortName", sortName)
                .append("searchName", searchName)
                .toString();
    }
}
