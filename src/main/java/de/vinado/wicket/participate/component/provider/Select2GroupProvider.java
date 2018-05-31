package de.vinado.wicket.participate.component.provider;

import de.vinado.wicket.participate.data.Group;
import de.vinado.wicket.participate.service.PersonService;
import org.apache.wicket.util.string.Strings;
import org.wicketstuff.select2.ChoiceProvider;
import org.wicketstuff.select2.Response;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class Select2GroupProvider extends ChoiceProvider<Group> {

    private PersonService personService;

    public Select2GroupProvider(final PersonService personService) {
        this.personService = personService;
    }

    @Override
    public String getDisplayValue(final Group group) {
        return group.getName();
    }

    @Override
    public String getIdValue(final Group group) {
        return group.getId().toString();
    }

    @Override
    public void query(final String term, final int page, final Response<Group> response) {
        if (!Strings.isEmpty(term)) {
            response.addAll(personService.findGroups("%" + term + "%"));
        } else {
            response.addAll(personService.getVisibleGroupList());
        }
        response.setHasMore(false);
    }

    @Override
    public Collection<Group> toChoices(final Collection<String> ids) {
        final ArrayList<Group> groupList = new ArrayList<>();

        for (String id : ids) {
            groupList.add(personService.load(Group.class, Long.parseLong(id)));
        }

        return groupList;
    }
}
