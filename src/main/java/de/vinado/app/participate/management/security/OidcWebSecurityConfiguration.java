package de.vinado.app.participate.management.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

@Configuration
@Profile("oidc")
public class OidcWebSecurityConfiguration extends Oauth2WebSecurityConfigurationSupport {

    public OidcWebSecurityConfiguration(ClientRegistrationRepository clientRegistrationRepository) {
        super(clientRegistrationRepository);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/_/**").hasAnyRole("admin", "organizer")
                .anyRequest().permitAll());
    }
}
