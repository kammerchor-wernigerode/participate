package de.vinado.wicket.participate.components;

import de.vinado.wicket.participate.ParticipateSession;
import de.vinado.wicket.participate.model.Person;
import de.vinado.wicket.participate.model.User;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

/**
 * @author Vincent Nadoll
 */
@Component
public class WicketSessionPersonContext implements PersonContext {

    @Nullable
    @Override
    public Person get() {
        ParticipateSession session = ParticipateSession.get();
        User user = session.getUser();
        return user.getPerson();
    }
}
