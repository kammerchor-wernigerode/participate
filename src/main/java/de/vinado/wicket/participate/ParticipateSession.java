package de.vinado.wicket.participate;

import de.vinado.wicket.participate.data.Event;
import de.vinado.wicket.participate.data.User;
import de.vinado.wicket.participate.data.filters.EventFilter;
import de.vinado.wicket.participate.services.EventService;
import de.vinado.wicket.participate.services.UserService;
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
        setEvent(eventService.getLatestEvent());
        setEventFilter(new EventFilter());
        setUser(userService.getUser4Username(ParticipateSession.get().getUser().getUsername()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean authenticate(final String usernameOrEmail, final String plainPassword) {
        final User user = userService.getAuthenticatedUser(usernameOrEmail, plainPassword);
        if (null != user) {
            this.user = user;
            if (eventService.hasUpcomingEvents()) {
                this.event = eventService.getLatestEvent();
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
}
