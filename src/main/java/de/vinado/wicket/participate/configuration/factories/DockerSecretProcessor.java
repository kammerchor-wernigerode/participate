package de.vinado.wicket.participate.configuration.factories;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Optional;
import java.util.Properties;

/**
 * Reads Docker Secret files from the local system an passes their values to the configured Spring properties.
 *
 * @author Vincent Nadoll
 */
public abstract class DockerSecretProcessor implements EnvironmentPostProcessor {

    public static final String PROPERTY_SOURCE = "dockerSecrets";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Optional<FileSystemResource> resource = getDockerSecretResource(environment);
        if (!resource.isPresent()) {
            return;
        }

        System.out.printf("[Docker Secret] Using %s from injected Docker secret file%n", getName());

        String secret = extractSecretValue(resource.get());
        Properties properties = configureProperties(secret);

        configure(environment, properties);
    }

    private Optional<FileSystemResource> getDockerSecretResource(ConfigurableEnvironment environment) {
        return getEnvironmentProperty(environment)
            .map(FileSystemResource::new)
            .filter(FileSystemResource::exists);
    }

    private Optional<String> getEnvironmentProperty(ConfigurableEnvironment environment) {
        return Optional.ofNullable(environment.getProperty(getEnvironmentVariable()))
            .map(StringUtils::trim);
    }

    protected abstract String getEnvironmentVariable();

    protected abstract String getName();

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

    private Properties configureProperties(String secret) {
        String springPropertyName = getSpringProperty();
        Properties properties = new Properties();

        properties.put(springPropertyName, secret);
        return properties;
    }

    protected abstract String getSpringProperty();

    private void configure(ConfigurableEnvironment environment, Properties properties) {
        PropertiesPropertySource propertySource = new PropertiesPropertySource(PROPERTY_SOURCE, properties);

        environment.getPropertySources().addLast(propertySource);
    }
}
