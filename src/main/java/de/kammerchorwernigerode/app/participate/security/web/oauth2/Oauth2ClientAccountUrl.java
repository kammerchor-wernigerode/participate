package de.kammerchorwernigerode.app.participate.security.web.oauth2;

import de.kammerchorwernigerode.app.participate.security.core.AccountUrl;
import de.kammerchorwernigerode.app.participate.security.web.oauth2.OidcClientProperties.Registration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Map;
import java.util.function.Function;

import lombok.RequiredArgsConstructor;

@Profile("oauth2")
@Component
@RequiredArgsConstructor
class Oauth2ClientAccountUrl implements AccountUrl {

    private final Oauth2ClientAuthenticationResolver oauth2ClientAuthenticationResolver;
    private final OidcClientProperties oidcClientProperties;

    @Override
    public URI get() {
        return oauth2ClientAuthenticationResolver.getOauth2AuthenticationToken()
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
