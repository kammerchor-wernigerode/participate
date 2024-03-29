package de.vinado.wicket.participate.components;

import de.vinado.app.participate.management.wicket.ManagementSession;
import de.vinado.wicket.participate.model.Person;
import de.vinado.wicket.participate.model.User;
import org.apache.wicket.Session;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component
public class WicketSessionPersonContext implements PersonContext {

    @Nullable
    @Override
    public Person get() {
        Session session = Session.get();
        User user = session.getMetaData(ManagementSession.user);
        return user.getPerson();
    }
}
