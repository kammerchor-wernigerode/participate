package de.kammerchorwernigerode.app.participate.security.support;

import de.kammerchorwernigerode.app.participate.security.AuthenticationResolver;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

import lombok.RequiredArgsConstructor;

@Profile("!oauth2")
@Component
@RequiredArgsConstructor
public class DefaultAuthenticationResolver implements AuthenticationResolver {

    private final AuthenticationHolder authentication;

    protected DefaultAuthenticationResolver() {
        this(() -> SecurityContextHolder.getContext().getAuthentication());
    }

    @Override
    public AuthenticatedPrincipal resolveUser() throws IndeterminableAuthenticationException {
        return authentication()
            .filter(Authentication::isAuthenticated)
            .map(AuthenticationAdapter::new)
            .orElseThrow(IndeterminableAuthenticationException::new);
    }

    protected Optional<Authentication> authentication() {
        return Optional.ofNullable(authentication.get());
    }


    @RequiredArgsConstructor
    private static class AuthenticationAdapter implements AuthenticatedPrincipal {

        private final Authentication authentication;

        @Override
        public String getName() {
            return authentication.getName();
        }
    }
}
