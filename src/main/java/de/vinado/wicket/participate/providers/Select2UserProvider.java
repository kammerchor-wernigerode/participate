package de.vinado.wicket.participate.providers;

import de.vinado.wicket.participate.model.User;
import de.vinado.wicket.participate.services.UserService;
import lombok.RequiredArgsConstructor;
import org.wicketstuff.select2.ChoiceProvider;
import org.wicketstuff.select2.Response;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Provides a list of users for a substring of their {@code username}.
 *
 * @author Vincent Nadoll
 */
@RequiredArgsConstructor
public class Select2UserProvider extends ChoiceProvider<User> {

    private final UserService userService;

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
        response.addAll(userService.findUsers(term));
        response.setHasMore(false);
    }

    @Override
    public Collection<User> toChoices(Collection<String> ids) {
        return ids.stream()
            .map(Long::parseLong)
            .map(userService::getUser)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }
}
