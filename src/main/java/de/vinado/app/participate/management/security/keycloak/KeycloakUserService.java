package de.vinado.app.participate.management.security.keycloak;

import org.springframework.context.annotation.Profile;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Profile("keycloak")
@Service
public class KeycloakUserService extends OidcUserService {

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);

        Set<GrantedAuthority> authorities = authorities(userRequest, oidcUser);
        OidcIdToken idToken = oidcUser.getIdToken();
        OidcUserInfo userInfo = oidcUser.getUserInfo();
        return new DefaultOidcUser(authorities, idToken, userInfo, StandardClaimNames.PREFERRED_USERNAME);
    }

    private Set<GrantedAuthority> authorities(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
        String clientId = clientId(userRequest);
        Set<GrantedAuthority> authorities = roles(oauth2User, clientId)
            .map(prepend("ROLE_"))
            .map(SimpleGrantedAuthority::new)
            .map(GrantedAuthority.class::cast)
            .collect(Collectors.toSet());
        authorities.addAll(oauth2User.getAuthorities());
        return authorities;
    }

    private String clientId(OAuth2UserRequest userRequest) {
        return userRequest.getClientRegistration().getClientId();
    }

    @SuppressWarnings("unchecked")
    private Stream<String> roles(OAuth2User oauth2User, String clientId) {
        Map<String, Object> attributes = oauth2User.getAttributes();
        return Optional.ofNullable(attributes.get("resource_access"))
            .filter(Map.class::isInstance)
            .map(Map.class::cast)
            .map(resourceAccess -> resourceAccess.get(clientId))
            .filter(Map.class::isInstance)
            .map(Map.class::cast)
            .map(client -> client.get("roles"))
            .filter(Collection.class::isInstance)
            .map(Collection.class::cast)
            .stream().flatMap(Collection::stream)
            .map(Object::toString);
    }

    private static UnaryOperator<String> prepend(String prefix) {
        return suffix -> prefix + suffix;
    }
}
