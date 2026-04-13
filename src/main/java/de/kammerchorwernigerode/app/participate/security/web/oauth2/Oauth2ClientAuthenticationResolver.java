package de.kammerchorwernigerode.app.participate.security.web.oauth2;

import de.kammerchorwernigerode.app.participate.security.support.DefaultAuthenticationResolver;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Component;

import java.util.Optional;

import lombok.RequiredArgsConstructor;

@Profile("oauth2")
@Component
class Oauth2ClientAuthenticationResolver extends DefaultAuthenticationResolver {

    @Override
    public AuthenticatedPrincipal resolveUser() throws IndeterminableAuthenticationException {
        return getOauth2AuthenticationToken()
            .map(OAuth2AuthenticationToken::getPrincipal)
            .filter(DefaultOidcUser.class::isInstance)
            .map(DefaultOidcUser.class::cast)
            .map(AuthenticatedOauth2User::new)
            .orElseThrow(IndeterminableAuthenticationException::new);
    }

    public Optional<OAuth2AuthenticationToken> getOauth2AuthenticationToken() {
        return authentication()
            .filter(OAuth2AuthenticationToken.class::isInstance)
            .map(OAuth2AuthenticationToken.class::cast);
    }


    @RequiredArgsConstructor
    private static class AuthenticatedOauth2User implements AuthenticatedPrincipal {

        private final DefaultOidcUser subject;

        @Override
        public String getName() {
            OidcUserInfo userInfo = subject.getUserInfo();
            return Optional.ofNullable(userInfo.getFullName())
                .orElseGet(subject::getName);
        }
    }
}
