package de.vinado.app.participate.common.security.web;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

public class WebSecurityConfigurationSupport {

    @Bean
    public SecurityFilterChain rootFilterChain(HttpSecurity http) throws Exception {
        http.securityMatchers(matchers -> matchers
                .requestMatchers("/**"))
            .csrf(AbstractHttpConfigurer::disable)
            .cors(Customizer.withDefaults())
            .authorizeHttpRequests(authorize -> authorize
                .anyRequest().permitAll());
        ;

        configure(http);
        return http.build();
    }

    protected void configure(HttpSecurity http) throws Exception {
    }
}
