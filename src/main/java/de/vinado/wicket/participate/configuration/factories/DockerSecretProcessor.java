package de.vinado.wicket.participate.configuration.factories;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.FileSystemResource;
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
        Optional<FileSystemResource> resource = Optional.ofNullable(environment.getProperty(getEnvironmentVariable()))
            .map(StringUtils::trim)
            .map(FileSystemResource::new)
            .filter(FileSystemResource::exists);

        if (!resource.isPresent()) {
            return;
        }

        System.out.printf("[Docker Secret] Using %s from injected Docker secret file%n", getName());

        try (InputStream secretInputStream = resource.get().getInputStream()) {
            String secret = StreamUtils.copyToString(secretInputStream, Charset.defaultCharset()).trim();
            Properties properties = new Properties();
            properties.put(getSpringProperty(), secret);

            PropertiesPropertySource propertySource = new PropertiesPropertySource(PROPERTY_SOURCE, properties);
            environment.getPropertySources().addLast(propertySource);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    protected abstract String getName();

    }

    protected abstract String getEnvironmentVariable();

    protected abstract String getSpringProperty();
}
