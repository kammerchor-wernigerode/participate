package de.kammerchorwernigerode.app.participate.security.web.oauth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import de.kammerchorwernigerode.app.participate.security.web.oauth2.OidcClientProperties.Registration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import static org.springframework.security.oauth2.core.OAuth2ErrorCodes.INVALID_TOKEN;

@Slf4j
@Profile("oauth2")
@Service
@EnableConfigurationProperties(OidcClientProperties.class)
public class ConfigurableOidcUserService extends OidcUserService {

    private static final String ROLE_PREFIX = "ROLE_";

    private final Configuration configuration;
    private final OidcClientProperties properties;

    public ConfigurableOidcUserService(ObjectMapper objectMapper, OidcClientProperties properties) {
        this.configuration = configuration(objectMapper);
        this.properties = properties;
    }

    private Configuration configuration(ObjectMapper objectMapper) {
        JacksonJsonProvider jsonProvider = new JacksonJsonProvider(objectMapper);
        return Configuration.builder()
            .jsonProvider(jsonProvider)
            .build();
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser user = super.loadUser(userRequest);

        Collection<? extends GrantedAuthority> authorities = authorities(userRequest, user);
        OidcUserInfo userInfo = user.getUserInfo();
        return createUser(userRequest, userInfo, authorities);
    }

    private Collection<? extends GrantedAuthority> authorities(OidcUserRequest userRequest,
                                                               OAuth2AuthenticatedPrincipal principal) {
        String registrationId = registrationId(userRequest);
        return registration(registrationId)
            .map(authoritiesOf(principal))
            .orElseGet(principal::getAuthorities);
    }

    private Set<GrantedAuthority> authorities(OAuth2AuthenticatedPrincipal principal, Registration registration) {
        List<String> roles = roles(principal, registration);
        Set<GrantedAuthority> authorities = roles.stream()
            .distinct()
            .map(prepend(ROLE_PREFIX))
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toSet());
        authorities.addAll(principal.getAuthorities());
        return authorities;
    }

    private String registrationId(OidcUserRequest userRequest) {
        return userRequest.getClientRegistration().getRegistrationId();
    }

    private Optional<Registration> registration(String registrationId) {
        Map<String, Registration> registration = properties.getRegistration();
        return Optional.ofNullable(registration.get(registrationId));
    }

    private Function<Registration, Collection<? extends GrantedAuthority>> authoritiesOf(
        OAuth2AuthenticatedPrincipal principal) {
        return registration -> authorities(principal, registration);
    }

    private List<String> roles(OAuth2AuthenticatedPrincipal principal, Registration registration) {
        try {
            String rolesJsonPath = registration.getRolesJsonPath();
            if (!StringUtils.hasText(rolesJsonPath)) {
                log.debug("No roles json path found for registration {}", registration);
                return Collections.emptyList();
            }

            return JsonPath.using(configuration)
                .parse(principal.getAttributes())
                .read(rolesJsonPath);
        } catch (PathNotFoundException e) {
            log.error("Expected claim {} to be present in JWT", registration.getRolesJsonPath(), e);
            OAuth2Error error = new OAuth2Error(INVALID_TOKEN);
            throw new OAuth2AuthenticationException(error, "Could not resolve roles from JWT", e);
        }
    }

    private OidcUser createUser(OidcUserRequest userRequest, OidcUserInfo userInfo,
                                Collection<? extends GrantedAuthority> authorities) {
        ClientRegistration.ProviderDetails providerDetails = userRequest.getClientRegistration().getProviderDetails();
        String userNameAttributeName = providerDetails.getUserInfoEndpoint().getUserNameAttributeName();
        return StringUtils.hasText(userNameAttributeName)
            ? new DefaultOidcUser(authorities, userRequest.getIdToken(), userInfo, userNameAttributeName)
            : new DefaultOidcUser(authorities, userRequest.getIdToken(), userInfo);
    }

    private static UnaryOperator<String> prepend(String prefix) {
        return suffix -> prefix + suffix;
    }
}
