package de.vinado.app.participate.management.security;

import org.springframework.security.core.AuthenticatedPrincipal;

@FunctionalInterface
public interface AuthenticationResolver {

    AuthenticatedPrincipal getAuthenticatedPrincipal();
}
