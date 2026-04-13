package de.kammerchorwernigerode.app.participate.security.web.oauth2;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties("app.security.oauth2.client")
public class OidcClientProperties {

    private final Map<String, Registration> registration = new HashMap<>();


    @Getter
    @Setter
    public static class Registration {

        private URI accountUrl;

        private String rolesJsonPath;
    }
}
