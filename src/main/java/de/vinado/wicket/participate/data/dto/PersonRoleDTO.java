package de.vinado.wicket.participate.data.dto;

import de.vinado.wicket.participate.data.Person;
import de.vinado.wicket.participate.data.permission.Role;

import java.io.Serializable;
import java.util.List;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class PersonRoleDTO implements Serializable {

    private Role role;

    private List<Person> persons;

    public PersonRoleDTO(final Role role, final List<Person> persons) {
        this.role = role;
        this.persons = persons;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(final Role role) {
        this.role = role;
    }

    public List<Person> getPersons() {
        return persons;
    }

    public void setPersons(final List<Person> persons) {
        this.persons = persons;
    }
}
