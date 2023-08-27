package de.vinado.wicket.participate;

import de.vinado.app.participate.common.wicket.utils.Holder;
import de.vinado.app.participate.management.security.AuthenticationResolver;
import de.vinado.wicket.participate.model.Event;
import de.vinado.wicket.participate.model.User;
import de.vinado.wicket.participate.model.filters.EventFilter;
import de.vinado.wicket.participate.services.EventService;
import de.vinado.wicket.participate.services.UserService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.Session;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.request.Request;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Custom {@link AuthenticatedWebSession}. This class based on a username and password, and a method implementation that
 * gets the Roles.
 *
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class ParticipateSession extends AbstractAuthenticatedWebSession {

    public static MetaDataKey<User> user = new MetaDataKey<>() { };
    public static MetaDataKey<Event> event = new MetaDataKey<>() { };
    public static MetaDataKey<EventFilter> eventFilter = new MetaDataKey<>() { };

    @SpringBean
    private Holder<AuthenticationResolver> authenticationResolver;

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

        setMetaData(event, eventService.getLatestEvent());
        setMetaData(eventFilter, new EventFilter());
        setMetaData(user, resolveUser());
    }

    /**
     * @return Returns the custom session
     */
    public static ParticipateSession get() {
        return (ParticipateSession) Session.get();
    }

    @Override
    public void onInvalidate() {
        super.onInvalidate();

        setMetaData(event, eventService.getLatestEvent());
        setMetaData(eventFilter, new EventFilter());
        setMetaData(user, resolveUser());
    }

    private User resolveUser() {
        AuthenticationResolver authenticationResolver = this.authenticationResolver.service();
        AuthenticatedPrincipal principal = authenticationResolver.getAuthenticatedPrincipal();
        return convertFrom(principal);
    }

    private User convertFrom(AuthenticatedPrincipal principal) {
        return userService.getUser(principal.getName());
    }

    @Override
    public Roles getRoles() {
        if (!isSignedIn()) {
            return new Roles();
        }

        return SecurityContextHolder.getContext().getAuthentication()
            .getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .filter(byPrefix("ROLE_"))
            .map(ParticipateSession::leadingRoleAbsent)
            .map(String::toUpperCase)
            .collect(Collectors.collectingAndThen(Collectors.joining(","), Roles::new));
    }

    private static String leadingRoleAbsent(String authority) {
        return authority.replaceFirst("^ROLE_", "");
    }

    private Predicate<String> byPrefix(String prefix) {
        return self -> self.startsWith(prefix);
    }

    @Override
    public boolean isSignedIn() {
        return true;
    }


    @Getter
    @Component
    @RequiredArgsConstructor
    static class AuthenticationResolverHolder implements Holder<AuthenticationResolver> {

        @Accessors(fluent = true)
        private final AuthenticationResolver service;
    }
}
