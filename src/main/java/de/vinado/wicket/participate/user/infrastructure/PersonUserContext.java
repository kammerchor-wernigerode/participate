package de.vinado.wicket.participate.user.infrastructure;

import de.vinado.wicket.participate.components.PersonContext;
import de.vinado.wicket.participate.model.Person;
import de.vinado.wicket.participate.model.User;
import de.vinado.wicket.participate.user.model.UserAdaptationService;
import de.vinado.wicket.participate.user.model.UserContext;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
class PersonUserContext implements UserContext {

    private final @NonNull PersonContext context;
    private final @NonNull UserAdaptationService adapterService;

    @Override
    public User get() {
        Person person = context.get();
        return adapterService.getUser(person).orElse(null);
    }
}
