package de.kammerchorwernigerode.app.participate.security;

import de.kammerchorwernigerode.app.participate.security.support.AuthenticationHolder;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Component;

import java.util.Optional;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthenticationResolver {

    private final AuthenticationHolder authentication;

    protected AuthenticationResolver() {
        this(() -> SecurityContextHolder.getContext().getAuthentication());
    }

    public AuthenticatedPrincipal resolveUser() {
        return getOauth2AuthenticationToken()
            .map(OAuth2AuthenticationToken::getPrincipal)
            .filter(DefaultOidcUser.class::isInstance)
            .map(DefaultOidcUser.class::cast)
            .map(AuthenticatedOauth2User::new)
            .orElseThrow(() -> new IllegalStateException("Expected user to be authenticated with OIDC"));
    }

    protected Optional<Authentication> authentication() {
        return Optional.ofNullable(authentication.get());
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
