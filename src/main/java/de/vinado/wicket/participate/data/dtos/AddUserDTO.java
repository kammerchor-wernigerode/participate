package de.vinado.wicket.participate.data.dtos;

import de.vinado.wicket.participate.data.Person;
import de.vinado.wicket.participate.data.User;

import java.io.Serializable;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class AddUserDTO implements Serializable {

    private User user;

    private String username;

    private String password;

    private String confirmPassword;

    private Person person;

    private String firstName;

    private String lastName;

    private String email;

    public AddUserDTO() {

    }

    public AddUserDTO(final User user) {
        this.user = user;
        this.username = user.getUsername();
    }

    public User getUser() {
        return user;
    }

    public void setUser(final User user) {
        this.user = user;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(final String confirmPassword) {
        this.confirmPassword = confirmPassword;
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
