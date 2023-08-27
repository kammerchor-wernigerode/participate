package de.vinado.app.participate.management.security;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.stereotype.Service;

@Primary
@Service
@RequiredArgsConstructor
public class EventPublishingAuthenticationResolver implements AuthenticationResolver {

    @NonNull
    private final AuthenticationResolver subject;
    @NonNull
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public AuthenticatedPrincipal getAuthenticatedPrincipal() {
        AuthenticatedPrincipal principal = subject.getAuthenticatedPrincipal();
        eventPublisher.publishEvent(new PrincipalAuthenticatedEvent(principal));
        return principal;
    }
}
