package de.vinado.wicket.participate.configuration.factories;

import de.vinado.boot.secrets.PropertyIndexSupplier;
import de.vinado.boot.secrets.SecretsEnvironmentPostProcessor;
import org.springframework.boot.logging.DeferredLogFactory;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.HashMap;
import java.util.Map;

/**
 * Reads Docker Secret files from the local system and passes their values to the configured Spring properties.
 *
 * @author Vincent Nadoll
 */
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

        return PropertyIndexSupplier.from(properties)
            .substituteValues(environment);
    }
}
