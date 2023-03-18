package de.vinado.wicket.participate.user.infrastructure;

import de.vinado.wicket.participate.model.Person;
import de.vinado.wicket.participate.model.User;
import de.vinado.wicket.participate.services.UserService;
import de.vinado.wicket.participate.user.model.UserAdaptationService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
class SimpleUserAdaptationService implements UserAdaptationService {

    private final @NonNull UserService userService;

    @Override
    public Optional<Person> getPerson(User user) {
        return Optional.ofNullable(user)
            .map(User::getPerson);
    }

    @Override
    public Optional<User> getUser(Person person) {
        try {
            User user = userService.getUser(person);
            return Optional.ofNullable(user);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
