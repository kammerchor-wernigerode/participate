package de.kammerchorwernigerode.app.participate.wicket.management;

import de.kammerchorwernigerode.app.participate.event.infrastructure.EventRecordRepository;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.request.Request;
import org.apache.wicket.util.string.Strings;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;

public class ManagementWicketSession extends AbstractAuthenticatedWebSession {

    public static MetaDataKey<Long> selectedEventId = new MetaDataKey<>() { };

    private final Environment environment;
    private final EventRecordRepository eventRecordRepository;

    public ManagementWicketSession(Request request, Environment environment,
                                   EventRecordRepository eventRecordRepository) {
        super(request);
        this.environment = environment;
        this.eventRecordRepository = eventRecordRepository;

        setMetaData(selectedEventId, getNextEventId());
    }

    @Override
    public void onInvalidate() {
        super.onInvalidate();

        setMetaData(selectedEventId, getNextEventId());
    }

    private Long getNextEventId() {
        return eventRecordRepository.findFirstIdByEndInstantGreaterThanEqualOrderByStartInstantAsc(Instant.now())
            .orElse(null);
    }

    @Override
    public Roles getRoles() {
        if (!isSignedIn()) {
            return new Roles();
        } else if (!environment.matchesProfiles("oauth2")) {
            return new AlwaysAuthorizedRoles();
        }

        Collection<? extends GrantedAuthority> authorities = getAuthentication()
            .map(Authentication::getAuthorities)
            .orElseGet(Collections::emptyList);
        return authorities.stream()
            .map(GrantedAuthority::getAuthority)
            .filter(not(Strings::isEmpty))
            .filter(byPrefix("ROLE_"))
            .map(ManagementWicketSession::leadingRoleAbsent)
            .map(String::toUpperCase)
            .collect(Collectors.collectingAndThen(Collectors.joining(","), Roles::new));
    }

    @Override
    public boolean isSignedIn() {
        return !environment.matchesProfiles("oauth2")
            || getAuthentication()
            .map(Authentication::isAuthenticated)
            .orElse(false);
    }

    private Optional<Authentication> getAuthentication() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(securityContext.getAuthentication());
    }

    private static Predicate<String> byPrefix(String prefix) {
        return self -> self.startsWith(prefix);
    }

    private static String leadingRoleAbsent(String authority) {
        return authority.replaceFirst("^ROLE_", "");
    }


    private static class AlwaysAuthorizedRoles extends Roles {

        @Override
        public boolean hasRole(String role) {
            return true;
        }

        @Override
        public boolean hasAnyRole(Roles roles) {
            return true;
        }

        @Override
        public boolean hasAllRoles(Roles roles) {
            return true;
        }

        @Override
        public String toString() {
            return "*";
        }
    }
}
