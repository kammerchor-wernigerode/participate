package de.vinado.wicket.participate.model.dtos;

import de.vinado.wicket.participate.model.Person;
import de.vinado.wicket.participate.model.User;
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
public class AddUserDTO implements Serializable {

    private User user;
    private String username;
    private String password;
    private String confirmPassword;
    private Person person;
    private String firstName;
    private String lastName;
    private String email;

    public AddUserDTO(User user) {
        this.user = user;
        this.username = user.getUsername();
    }
}
