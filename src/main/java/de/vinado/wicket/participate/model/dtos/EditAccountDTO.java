package de.vinado.wicket.participate.model.dtos;

import de.vinado.wicket.participate.model.Person;
import de.vinado.wicket.participate.model.Singer;
import de.vinado.wicket.participate.model.User;
import de.vinado.wicket.participate.model.Voice;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import javax.annotation.Nullable;

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

    public EditAccountDTO(User user, @Nullable final Person person, @Nullable final Singer singer) {
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
