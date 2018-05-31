package de.vinado.wicket.participate.data.dto;

import de.vinado.wicket.participate.data.Group;
import de.vinado.wicket.participate.data.Member;

import java.io.Serializable;
import java.util.List;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class MemberToGroupDTO implements Serializable {

    private Group group;

    private List<Member> members;


    public MemberToGroupDTO(final Group group, final List<Member> members) {
        this.group = group;
        this.members = members;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(final Group group) {
        this.group = group;
    }

    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(final List<Member> members) {
        this.members = members;
    }
}
