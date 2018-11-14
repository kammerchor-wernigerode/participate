package de.vinado.wicket.participate.providers;

import de.vinado.wicket.participate.model.User;
import de.vinado.wicket.participate.services.UserService;
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
        final ArrayList<User> users = new ArrayList<>();
        for (String id : ids) {
            users.add(userService.getUser(Long.parseLong(id)));
        }
        return users;
    }
}
