package de.vinado.wicket.participate;

import de.vinado.wicket.participate.model.Event;
import de.vinado.wicket.participate.model.User;
import de.vinado.wicket.participate.model.filters.EventFilter;
import de.vinado.wicket.participate.services.EventService;
import de.vinado.wicket.participate.services.UserService;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.Session;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.request.Request;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * Custom {@link AuthenticatedWebSession}. This class based on a username and password, and a method implementation that
 * gets the Roles.
 *
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class ParticipateSession extends AuthenticatedWebSession {

    public static MetaDataKey<User> user = new MetaDataKey<>() { };
    public static MetaDataKey<Event> event = new MetaDataKey<>() { };
    public static MetaDataKey<EventFilter> eventFilter = new MetaDataKey<>() { };

    @SuppressWarnings("unused")
    @SpringBean
    private UserService userService;

    @SuppressWarnings("unused")
    @SpringBean
    private EventService eventService;

    /**
     * @param request {@link Request}
     */
    public ParticipateSession(final Request request) {
        super(request);
        Injector.get().inject(this);
    }

    /**
     * @return Returns the custom session
     */
    public static ParticipateSession get() {
        return (ParticipateSession) Session.get();
    }

    public void clearSessionData() {
        setMetaData(event, eventService.getLatestEvent());
        setMetaData(eventFilter, new EventFilter());
        setMetaData(user, userService.getUser(getMetaData(user).getUsername()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean authenticate(final String usernameOrEmail, final String plainPassword) {
        final User user = userService.getUser(usernameOrEmail, plainPassword);
        if (null != user) {
            setMetaData(ParticipateSession.user, user);
            if (eventService.hasUpcomingEvents()) {
                setMetaData(event, eventService.getLatestEvent());
            }

            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Roles getRoles() {
        final Roles roles = new Roles();

        if (isSignedIn())
            roles.add(Roles.USER);
        if (getMetaData(user).isAdmin())
            roles.add(Roles.ADMIN);
        return roles;
    }
}
