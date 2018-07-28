package de.vinado.wicket.participate.model.dtos;

import de.vinado.wicket.participate.model.Person;

import java.io.Serializable;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class PersonDTO implements Serializable {

    private Person person;

    private String firstName;

    private String lastName;

    private String email;

    public PersonDTO() {
    }

    public PersonDTO(final Person person) {
        this.person = person;
        this.firstName = person.getFirstName();
        this.lastName = person.getLastName();
        this.email = person.getEmail();
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(final Person person) {
        this.person = person;
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
}
