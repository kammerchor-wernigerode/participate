package de.vinado.wicket.participate.configuration.factories;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.logging.DeferredLog;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Reads Docker Secret files from the local system an passes their values to the configured Spring properties.
 *
 * @author Vincent Nadoll
 */
@Component
public class DockerSecretProcessor implements EnvironmentPostProcessor, ApplicationListener<ApplicationEvent>, Ordered {

    public static final String PROPERTY_SOURCE = "dockerSecrets";

    private static final DeferredLog log = new DeferredLog();
    private static final Map<String, String> properties = new LinkedHashMap<>();

    static {
        properties.put("spring.datasource.username", "DATABASE_USER_FILE");
        properties.put("spring.datasource.password", "DATABASE_PASSWORD_FILE");
        properties.put("spring.mail.username", "SMTP_USER_FILE");
        properties.put("spring.mail.password", "SMTP_PASSWORD_FILE");
    }

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        log.trace("Examine for Docker Secret related environment variables");
        Map<String, Object> source = new LinkedHashMap<>();

        for (Map.Entry<String, String> entry : properties.entrySet()) {
            String environmentVariable = entry.getValue();
            log.trace(String.format("Resolving %s", environmentVariable));

            String dockerSecretPath = getEnvironmentProperty(environmentVariable, environment)
                .orElse(null);

            if (null == dockerSecretPath) {
                log.debug(String.format("Environment variable %s is not set. Using value from configuration file instead.", environmentVariable));
                continue;
            }

            log.trace(String.format("Reading Docker Secret from %s", dockerSecretPath));
            Optional<FileSystemResource> dockerSecret = getDockerSecretResource(dockerSecretPath);

            if (!dockerSecret.isPresent()) {
                log.warn(String.format("Docker Secret file %s not found or not readable.", dockerSecretPath));
                continue;
            }

            log.info(String.format("Use Docker Secret value to set %s", entry.getKey()));

            source.put(entry.getKey(), extractSecretValue(dockerSecret.get()));
        }

        environment.getPropertySources()
            .addAfter(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME,
                new MapPropertySource(PROPERTY_SOURCE, source));
    }

    private Optional<FileSystemResource> getDockerSecretResource(String dockerSecretPath) {
        return Optional.of(dockerSecretPath)
            .map(FileSystemResource::new)
            .filter(FileSystemResource::exists)
            .filter(FileSystemResource::isReadable);
    }

    private Optional<String> getEnvironmentProperty(String environmentVariable, ConfigurableEnvironment environment) {
        return Optional.ofNullable(environment.getProperty(environmentVariable))
            .map(StringUtils::trim);
    }

    private String extractSecretValue(Resource resource) {
        InputStream secretInputStream = getInputStream(resource);
        return getSecretValue(secretInputStream);
    }

    private InputStream getInputStream(Resource resource) {
        try {
            return resource.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getSecretValue(InputStream secretInputStream) {
        try {
            return StreamUtils.copyToString(secretInputStream, Charset.defaultCharset()).trim();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        log.replayTo(DockerSecretProcessor.class);
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
