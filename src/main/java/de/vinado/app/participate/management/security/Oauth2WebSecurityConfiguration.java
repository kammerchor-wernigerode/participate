package de.vinado.app.participate.management.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

@Slf4j
@Configuration
@Profile("oauth2 & !oidc")
public class Oauth2WebSecurityConfiguration extends Oauth2WebSecurityConfigurationSupport {

    public Oauth2WebSecurityConfiguration(ClientRegistrationRepository clientRegistrationRepository) {
        super(clientRegistrationRepository);
        log.warn("Using OAuth2 without OIDC! Any authenticated user can access the application.");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .anyRequest().authenticated());
    }
}
