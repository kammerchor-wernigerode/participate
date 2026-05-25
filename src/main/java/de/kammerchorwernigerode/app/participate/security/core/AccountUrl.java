package de.kammerchorwernigerode.app.participate.security.core;

import de.kammerchorwernigerode.app.participate.security.AuthenticationResolver;
import de.kammerchorwernigerode.app.participate.security.web.oauth2.OidcClientProperties;
import de.kammerchorwernigerode.app.participate.security.web.oauth2.OidcClientProperties.Registration;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AccountUrl implements Supplier<URI> {

    private final AuthenticationResolver authenticationResolver;
    private final OidcClientProperties oidcClientProperties;

    @Override
    public URI get() {
        return authenticationResolver.getOauth2AuthenticationToken()
            .map(OAuth2AuthenticationToken::getAuthorizedClientRegistrationId)
            .map(resolveRegistration(oidcClientProperties))
            .map(Registration::getAccountUrl)
            .orElse(null);
    }

    private static Function<String, Registration> resolveRegistration(OidcClientProperties properties) {
        return registrationId -> {
            Map<String, Registration> registration = properties.getRegistration();
            return registration.get(registrationId);
        };
    }
}
