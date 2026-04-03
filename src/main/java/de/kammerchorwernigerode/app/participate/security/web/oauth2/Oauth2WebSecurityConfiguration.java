package de.kammerchorwernigerode.app.participate.security.web.oauth2;

import de.kammerchorwernigerode.app.participate.security.web.WebSecurityConfigurationSupport;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import lombok.RequiredArgsConstructor;

@Profile("oauth2")
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
class Oauth2WebSecurityConfiguration extends WebSecurityConfigurationSupport {

    @Override
    protected void configure(HttpSecurity http) {
        http
            .oauth2Login(Customizer.withDefaults())
            .logout(logout -> logout
                .invalidateHttpSession(true)
                .permitAll())
        ;
    }
}
