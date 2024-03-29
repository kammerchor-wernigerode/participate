package de.vinado.app.participate.management.wicket;

import de.vinado.app.participate.management.security.AuthenticationResolver;
import de.vinado.app.participate.management.wicket.security.ManagementWicketSecurityProperties;
import de.vinado.wicket.participate.model.Event;
import de.vinado.wicket.participate.model.User;
import de.vinado.wicket.participate.model.filters.EventFilter;
import de.vinado.wicket.participate.services.EventService;
import de.vinado.wicket.participate.services.UserService;
import org.apache.wicket.Application;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.request.Request;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ManagementSession extends AbstractAuthenticatedWebSession {

    public static MetaDataKey<User> user = new MetaDataKey<>() { };
    public static MetaDataKey<Event> event = new MetaDataKey<>() { };
    public static MetaDataKey<EventFilter> eventFilter = new MetaDataKey<>() { };

    @SpringBean
    private AuthenticationResolver authenticationResolver;

    @SpringBean
    private UserService userService;

    @SpringBean
    private EventService eventService;

    @SpringBean
    private ManagementWicketSecurityProperties securityProperties;

    public ManagementSession(Request request) {
        super(request);
        Injector.get().inject(this);

        setMetaData(event, eventService.getLatestEvent());
        setMetaData(eventFilter, new EventFilter());
        setMetaData(user, resolveUser().orElseThrow(IllegalArgumentException::new));
    }

    @Override
    public void onInvalidate() {
        super.onInvalidate();

        setMetaData(event, eventService.getLatestEvent());
        setMetaData(eventFilter, new EventFilter());
        setMetaData(user, resolveUser().orElse(null));
    }

    private Optional<User> resolveUser() {
        return principal(authenticationResolver)
            .or(this::resolveFromProperty)
            .map(this::convertFrom);
    }

    private User convertFrom(AuthenticatedPrincipal principal) {
        return userService.getUser(principal.getName());
    }

    private Optional<AuthenticatedPrincipal> principal(AuthenticationResolver resolver) {
        try {
            return Optional.of(resolver.getAuthenticatedPrincipal());
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private Optional<User> resolveFromProperty() {
        if (!Application.exists()) {
            return Optional.empty();
        }

        if (getApplication().usesDeploymentConfig()) {
            return Optional.empty();
        }

        return Optional.ofNullable(securityProperties.getImpersonateUsername())
            .map(userService::getUser);
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
            .map(ManagementSession::leadingRoleAbsent)
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
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        return authentication.isAuthenticated();
    }
}
