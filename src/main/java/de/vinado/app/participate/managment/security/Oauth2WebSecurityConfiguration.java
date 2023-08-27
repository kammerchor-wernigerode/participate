package de.vinado.app.participate.managment.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Profile("oauth2")
@Configuration
@EnableWebSecurity
public class Oauth2WebSecurityConfiguration {

    @Bean
    public SecurityFilterChain managmentSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authorize -> authorize
                .antMatchers("/_/**").hasAnyRole("admin", "organizer")
                .anyRequest().permitAll())
            .oauth2Login(Customizer.withDefaults())
        ;

        return http.build();
    }
}
