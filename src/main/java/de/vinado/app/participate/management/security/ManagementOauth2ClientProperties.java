package de.vinado.app.participate.management.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@Getter
@ConfigurationProperties("app.security.oauth2.client")
class ManagementOauth2ClientProperties {

    private final Map<String, Registration> registration = new HashMap<>();


    @Getter
    @Setter
    public static class Registration {

        private String rolesJsonPath;
    }
}
