package de.vinado.wicket.participate.component.provider;

import de.vinado.wicket.participate.data.User;
import de.vinado.wicket.participate.service.UserService;
import org.wicketstuff.select2.ChoiceProvider;
import org.wicketstuff.select2.Response;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class Select2UserProvider extends ChoiceProvider<User> {

    private UserService userService;

    public Select2UserProvider(final UserService userService) {
        this.userService = userService;
    }

    @Override
    public String getDisplayValue(User user) {
        return user.getUsername();
    }

    @Override
    public String getIdValue(User user) {
        return user.getId().toString();
    }

    @Override
    public void query(String term, int page, Response<User> response) {
        response.addAll(userService.findUsers("%" + term + "%"));
        response.setHasMore(false);
    }

    @Override
    public Collection<User> toChoices(Collection<String> ids) {
        final ArrayList<User> personList = new ArrayList<>();
        for (String id : ids) {
            personList.add(userService.getUser4PersonId(Long.parseLong(id)));
        }
        return personList;
    }
}
