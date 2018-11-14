package de.vinado.wicket.participate.configuration;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

/**
 * Reads the properties from "classpath:/application.properties"
 *
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
@Validated
@Configuration
@ConfigurationProperties("app")
@EnableConfigurationProperties
@Getter
@Setter
public class ApplicationProperties {

    private boolean developmentMode = false;
    private String customer = "";
    private @NonNull String participatePassword;
    private String version;
    private Mail mail;
    private Database database;

    @Getter
    @Setter
    public static class Mail {

        private @NonNull String sender;
        private String footer = "Participate";
    }

    @Getter
    @Setter
    public static class Database {

        private boolean mirrorRemoteDatabase = false;
        private String remoteUrl;
        private String remoteUsername;
        private String remotePassword;
    }
}
