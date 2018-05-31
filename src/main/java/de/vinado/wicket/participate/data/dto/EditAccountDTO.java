package de.vinado.wicket.participate.data.dto;

import de.vinado.wicket.participate.data.Member;
import de.vinado.wicket.participate.data.Person;
import de.vinado.wicket.participate.data.User;
import de.vinado.wicket.participate.data.Voice;

import javax.annotation.Nullable;
import java.io.Serializable;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class EditAccountDTO implements Serializable {

    private User user;

    private Person person;

    private Member member;

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

    public EditAccountDTO(final User user, @Nullable final Person person, @Nullable final Member member) {
        this.user = user;
        this.person = person;
        this.member = member;
        this.username = user.getUsername();
        if (null != person) {
            this.firstName = person.getFirstName();
            this.lastName = person.getLastName();
            this.email = person.getEmail();
            if (null != member) {
                this.voice = member.getVoice();
            }
        }
    }

    public User getUser() {
        return user;
    }

    public void setUser(final User user) {
        this.user = user;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(final Person person) {
        this.person = person;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(final Member member) {
        this.member = member;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(final String oldPassword) {
        this.oldPassword = oldPassword;
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

    public Voice getVoice() {
        return voice;
    }

    public void setVoice(final Voice voice) {
        this.voice = voice;
    }
}
