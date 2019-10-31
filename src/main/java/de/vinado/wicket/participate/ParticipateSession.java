package de.vinado.wicket.participate;

import de.vinado.wicket.participate.model.Event;
import de.vinado.wicket.participate.model.User;
import de.vinado.wicket.participate.model.filters.EventFilter;
import de.vinado.wicket.participate.services.EventService;
import de.vinado.wicket.participate.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.wicket.Session;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.request.Request;
import org.apache.wicket.spring.injection.annot.SpringBean;

import javax.persistence.NoResultException;

/**
 * Custom {@link AuthenticatedWebSession}. This class based on a username and password, and a method implementation that
 * gets the Roles.
 *
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
@Slf4j
public class ParticipateSession extends AuthenticatedWebSession {

    @SuppressWarnings("unused")
    @SpringBean
    private UserService userService;

    @SuppressWarnings("unused")
    @SpringBean
    private EventService eventService;

    private User user;

    private Event event;

    private EventFilter eventFilter;

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
        setEventFilter(new EventFilter());
        setEvent(next());

        String username = ParticipateSession.get().getUser().getUsername();
        try {
            User user = userService.getUser(username);
            setUser(user);
        } catch (NoResultException e) {
            log.warn("Could not retrieve user w/ username={}", username);
            setUser(null);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean authenticate(final String usernameOrEmail, final String plainPassword) {
        final User user = userService.getUser(usernameOrEmail, plainPassword);
        if (null != user) {
            this.user = user;
            this.event = next();

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
        if (user.isAdmin())
            roles.add(Roles.ADMIN);
        return roles;
    }

    public User getUser() {
        return user;
    }

    public void setUser(final User user) {
        this.user = user;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(final Event event) {
        this.event = event;
    }

    public EventFilter getEventFilter() {
        return eventFilter;
    }

    public void setEventFilter(final EventFilter eventFilter) {
        this.eventFilter = eventFilter;
    }

    /**
     * @return the next upcoming event
     */
    private Event next() {
        try {
            return eventService.getLatestEvent();
        } catch (NoResultException e) {
            log.debug("Could not find next event");
            return null;
        }
    }
}
