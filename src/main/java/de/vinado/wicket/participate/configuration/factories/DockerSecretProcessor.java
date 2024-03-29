package de.vinado.wicket.participate.configuration.factories;

import de.vinado.boot.secrets.PropertyIndexSupplier;
import de.vinado.boot.secrets.SecretsEnvironmentPostProcessor;
import org.springframework.boot.logging.DeferredLogFactory;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.HashMap;
import java.util.Map;

public class DockerSecretProcessor extends SecretsEnvironmentPostProcessor {

    public DockerSecretProcessor(DeferredLogFactory logFactory) {
        super(logFactory);
    }

    @Override
    protected PropertyIndexSupplier getPropertyIndexSupplier(ConfigurableEnvironment environment) {
        Map<String, String> properties = new HashMap<>();
        properties.put("spring.datasource.username", "DATABASE_USER_FILE");
        properties.put("spring.datasource.password", "DATABASE_PASSWORD_FILE");
        properties.put("spring.mail.username", "SMTP_USER_FILE");
        properties.put("spring.mail.password", "SMTP_PASSWORD_FILE");
        properties.put("app.crypto.session-secret", "CRYPTO_SESSION_SECRET_FILE");
        properties.put("app.crypto.pbe-salt", "CRYPTO_PBE_SALT_FILE");
        properties.put("spring.security.oauth2.client.registration.keycloak.client-secret", "SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_KEYCLOAK_CLIENT_SECRET_FILE");

        return PropertyIndexSupplier.from(properties)
            .substituteValues(environment);
    }
}
