package de.vinado.wicket.participate.component.provider;

import de.vinado.wicket.participate.data.Member;
import de.vinado.wicket.participate.service.PersonService;
import org.wicketstuff.select2.ChoiceProvider;
import org.wicketstuff.select2.Response;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class Select2MemberProvider extends ChoiceProvider<Member> {

    private PersonService personService;

    public Select2MemberProvider(final PersonService personService) {
        this.personService = personService;
    }

    @Override
    public String getDisplayValue(final Member member) {
        return member.getPerson().getSearchName();
    }

    @Override
    public String getIdValue(final Member member) {
        return member.getId().toString();
    }

    @Override
    public void query(final String term, final int page, final Response<Member> response) {
        response.addAll(personService.findMembers("%" + term + "%"));
        response.setHasMore(false);
    }

    @Override
    public Collection<Member> toChoices(final Collection<String> ids) {
        final ArrayList<Member> memberList = new ArrayList<>();
        for (String id : ids) {
            memberList.add(personService.getMember(Long.parseLong(id)));
        }
        return memberList;
    }
}
