package de.vinado.wicket.participate.data.dto;


import de.vinado.wicket.participate.data.Group;
import de.vinado.wicket.participate.data.Member;
import de.vinado.wicket.participate.data.Person;
import de.vinado.wicket.participate.data.Voice;

import java.io.Serializable;
import java.util.List;

/**
 * DTO for a {@link de.vinado.wicket.participate.data.Member}
 *
 * @author Julius Felchow (julius.felchow@gmail.com)
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class MemberDTO implements Serializable {

    private Member member;

    private Voice voice;

    private Person person;

    private String firstName;

    private String lastName;

    private String email;

    private List<Group> groups;

    public MemberDTO() {
    }

    public MemberDTO(final Member member, final List<Group> groups) {
        this.member = member;
        this.voice = member.getVoice();
        this.person = member.getPerson();
        this.firstName = member.getPerson().getEmail();
        this.firstName = member.getPerson().getFirstName();
        this.lastName = member.getPerson().getLastName();
        this.email = member.getPerson().getEmail();
        this.groups = groups;
    }

    public Member getMember() {
        return member;
    }

    public void setMember(final Member member) {
        this.member = member;
    }

    public Voice getVoice() {
        return voice;
    }

    public void setVoice(final Voice voice) {
        this.voice = voice;
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

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(final List<Group> groups) {
        this.groups = groups;
    }
}
