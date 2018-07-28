package de.vinado.wicket.participate.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * Person
 *
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "persons")
public class Person implements Identifiable, Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "email", unique = true)
    private String email;

    @CreationTimestamp
    private Date creationDate;

    @UpdateTimestamp
    private Date lastModified;

    @Formula("CONCAT(first_name, ' ', last_name)")
    private String displayName;

    @Formula("CONCAT(last_name, ', ', first_name)")
    private String sortName;

    @Formula("CONCAT(first_name, ' ', last_name, ' (', COALESCE(email, 'Email ist nicht hinterlegt'), ')')")
    private String searchName;

    protected Person() {
    } // JPA only

    public Person(final Person person) {
        this.id = person.getId();
        this.lastName = person.getLastName();
        this.firstName = person.getFirstName();
        this.email = person.getEmail();
        this.creationDate = person.getCreationDate();
        this.lastModified = person.getLastModified();
        this.displayName = person.getDisplayName();
        this.sortName = person.getSortName();
        this.searchName = person.getSearchName();
    }

    public Person(final String lastName, final String firstName, final String email) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.email = email;
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
