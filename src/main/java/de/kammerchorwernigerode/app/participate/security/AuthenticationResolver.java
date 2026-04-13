package de.kammerchorwernigerode.app.participate.security;

import org.springframework.security.core.AuthenticatedPrincipal;

public interface AuthenticationResolver {

    AuthenticatedPrincipal resolveUser() throws IndeterminableAuthenticationException;


    class IndeterminableAuthenticationException extends IllegalArgumentException {

        public IndeterminableAuthenticationException() {
            super("Unable to resolve authentication");
        }
    }
}
