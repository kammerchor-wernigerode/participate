package de.vinado.app.participate.management.wicket;

import de.vinado.app.participate.common.wicket.utils.Holder;
import de.vinado.app.participate.management.security.AuthenticationResolver;
import de.vinado.app.participate.management.wicket.security.ManagementWicketSecurityProperties;
import de.vinado.wicket.participate.model.Event;
import de.vinado.wicket.participate.model.User;
import de.vinado.wicket.participate.model.filters.EventFilter;
import de.vinado.wicket.participate.services.EventService;
import de.vinado.wicket.participate.services.UserService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.injection.Injector;
import org.apache.wicket.request.Request;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ManagementSession extends AbstractAuthenticatedWebSession {

    public static MetaDataKey<User> user = new MetaDataKey<>() { };
    public static MetaDataKey<Event> event = new MetaDataKey<>() { };
    public static MetaDataKey<EventFilter> eventFilter = new MetaDataKey<>() { };

    @SpringBean
    private Holder<AuthenticationResolver> authenticationResolver;

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
        setMetaData(user, resolveUser());
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
        AuthenticatedPrincipal principal = principal(authenticationResolver)
            .or(this::resolveFromProperty)
            .orElseThrow(IllegalArgumentException::new);
        return convertFrom(principal);
    }

    private Optional<AuthenticatedPrincipal> principal(AuthenticationResolver resolver) {
        try {
            return Optional.of(resolver.getAuthenticatedPrincipal());
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private Optional<User> resolveFromProperty() {
        if (getApplication().usesDeploymentConfig()) {
            return Optional.empty();
        }

        return Optional.ofNullable(securityProperties.getImpersonateUsername())
            .map(userService::getUser);
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
