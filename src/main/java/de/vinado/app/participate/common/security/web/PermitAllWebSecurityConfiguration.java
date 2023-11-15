package de.vinado.app.participate.common.security.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Profile("!oauth2")
@EnableWebSecurity
@Configuration
public class PermitAllWebSecurityConfiguration extends WebSecurityConfigurationSupport {
}
