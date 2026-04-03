package de.kammerchorwernigerode.app.participate.security.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Profile("!oauth2")
@EnableWebSecurity
@Configuration
class AlwaysPermittedWebSecurityConfiguration extends WebSecurityConfigurationSupport {
}
