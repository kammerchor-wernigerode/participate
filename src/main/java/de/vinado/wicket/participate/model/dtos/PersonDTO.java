package de.vinado.wicket.participate.model.dtos;

import de.vinado.wicket.participate.model.Person;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
@Getter
@Setter
@RequiredArgsConstructor
public class PersonDTO implements Serializable {

    private Person person;
    private String firstName;
    private String lastName;
    private String email;

    public PersonDTO(final Person person) {
        this.person = person;
        this.firstName = person.getFirstName();
        this.lastName = person.getLastName();
        this.email = person.getEmail();
    }
}
