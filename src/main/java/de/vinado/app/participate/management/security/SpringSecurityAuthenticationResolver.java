package de.vinado.app.participate.management.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class SpringSecurityAuthenticationResolver implements AuthenticationResolver {

    private final AuthenticationHolder authentication;

    SpringSecurityAuthenticationResolver() {
        this(() -> SecurityContextHolder.getContext().getAuthentication());
    }

    @Override
    public AuthenticatedPrincipal getAuthenticatedPrincipal() {
        return convertFromOauth2Login()
            .orElseThrow(IllegalArgumentException::new);
    }

    private Optional<AuthenticatedPrincipal> convertFromOauth2Login() {
        return getAuthentication()
            .map(Authentication::getPrincipal)
            .filter(DefaultOidcUser.class::isInstance)
            .map(DefaultOidcUser.class::cast);
    }

    private Optional<Authentication> getAuthentication() {
        return Optional.ofNullable(authentication.get());
    }


    @FunctionalInterface
    public interface AuthenticationHolder extends Supplier<Authentication> {
    }
}
