package de.kammerchorwernigerode.app.participate.security.web;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

public class WebSecurityConfigurationSupport {

    @Bean
    public SecurityFilterChain rootFilterChain(HttpSecurity http) {
        http
            .securityMatcher(PathPatternRequestMatcher.withDefaults()
                .matcher("/**"))
            .authorizeHttpRequests(authorize -> authorize
                .anyRequest().permitAll())
        ;

        configure(http);
        return http.build();
    }

    protected void configure(HttpSecurity http) {
    }
}
