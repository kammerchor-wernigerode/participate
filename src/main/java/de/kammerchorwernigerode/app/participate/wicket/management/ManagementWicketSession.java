package de.kammerchorwernigerode.app.participate.wicket.management;

import de.kammerchorwernigerode.app.participate.event.infrastructure.EventRecordRepository;
import de.kammerchorwernigerode.app.participate.event.infrastructure.EventReference;
import de.kammerchorwernigerode.app.participate.event.presentation.ui.overview.details.AttendeeTable;
import de.kammerchorwernigerode.app.participate.person.presentation.ui.overview.PersonTable;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.request.Request;
import org.apache.wicket.util.string.Strings;
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

    private final EventRecordRepository eventRecordRepository;

    public ManagementWicketSession(Request request, EventRecordRepository eventRecordRepository) {
        super(request);
        this.eventRecordRepository = eventRecordRepository;

        setMetaData(selectedEventId, getNextEventId());
        setMetaData(AttendeeTable.attendeeTablePageSize, 15L);
        setMetaData(PersonTable.personTablePageSize, 25L);
    }

    @Override
    public void onInvalidate() {
        super.onInvalidate();

        setMetaData(selectedEventId, getNextEventId());
    }

    private Long getNextEventId() {
        return eventRecordRepository.findFirstByEndInstantGreaterThanEqualOrderByStartInstantAsc(Instant.now())
            .map(EventReference::getId)
            .orElse(null);
    }

    @Override
    public Roles getRoles() {
        if (!isSignedIn()) {
            return new Roles();
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
        return getAuthentication()
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
}
