package de.vinado.app.participate.management.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;

import java.util.ArrayList;

import static java.util.UUID.randomUUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SpringSecurityAuthenticationResolverTests {

    SpringSecurityAuthenticationResolver.AuthenticationHolder authenticationHolder;
    SpringSecurityAuthenticationResolver authenticationResolver;

    @BeforeEach
    void setUp() {
        authenticationHolder = mock(SpringSecurityAuthenticationResolver.AuthenticationHolder.class);
        authenticationResolver = new SpringSecurityAuthenticationResolver(authenticationHolder);
    }

    @Test
    void requestingUserWithoutAuthentication_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> authenticationResolver.getAuthenticatedPrincipal());
    }

    @Test
    void givenOauth2AuthToken_shouldResolvePrincipal() {
        when(authenticationHolder.get()).thenReturn(oauth2Authentication());

        assertNotNull(authenticationResolver.getAuthenticatedPrincipal());
    }

    public static Authentication oauth2Authentication() {
        OidcIdToken oidcToken = OidcIdToken
            .withTokenValue(randomUUID().toString())
            .claim(StandardClaimNames.SUB, randomUUID().toString())
            .build();
        DefaultOidcUser oidcUser = new DefaultOidcUser(new ArrayList<>(), oidcToken);
        return new OAuth2AuthenticationToken(oidcUser, new ArrayList<>(), randomUUID().toString());
    }
}
