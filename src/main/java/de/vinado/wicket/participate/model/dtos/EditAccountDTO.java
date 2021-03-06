package de.vinado.wicket.participate.model.dtos;

import de.vinado.wicket.participate.model.Person;
import de.vinado.wicket.participate.model.Singer;
import de.vinado.wicket.participate.model.User;
import de.vinado.wicket.participate.model.Voice;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;
import java.io.Serializable;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
@Getter
@Setter
public class EditAccountDTO implements Serializable {

    private User user;
    private Person person;
    private Singer singer;
    private String username;
    private String oldPassword;
    private String password;
    private String confirmPassword;
    private String firstName;
    private String lastName;
    private String email;
    private Voice voice;

    public EditAccountDTO(final User user) {
        this.user = user;
        this.username = user.getUsername();
    }

    public EditAccountDTO(final User user, @Nullable final Person person) {
        this.user = user;
        this.person = person;
        this.username = user.getUsername();
        if (null != person) {
            this.firstName = person.getFirstName();
            this.lastName = person.getLastName();
            this.email = person.getEmail();
        }
    }

    public EditAccountDTO(final User user, @Nullable final Person person, @Nullable final Singer singer) {
        this.user = user;
        this.person = person;
        this.singer = singer;
        this.username = user.getUsername();
        if (null != person) {
            this.firstName = person.getFirstName();
            this.lastName = person.getLastName();
            this.email = person.getEmail();
            if (null != singer) {
                this.voice = singer.getVoice();
            }
        }
    }
}
