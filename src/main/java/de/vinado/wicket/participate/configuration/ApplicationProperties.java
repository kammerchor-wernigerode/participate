package de.vinado.wicket.participate.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Reads the properties from "classpath:/application.properties"
 *
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
@Configuration
@ConfigurationProperties("app")
@Getter
@Setter
public class ApplicationProperties {

    private static final String EMAIL_PATTERN = "^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,6}$";

    private @NotBlank String baseUrl;
    private boolean developmentMode = false;
    private String customer = "";
    private @NotBlank String participatePassword;
    private String version;
    private Mail mail;
    private Database database;
    private Features features;

    @Getter
    @Setter
    public static class Mail {

        private @Pattern(regexp = EMAIL_PATTERN) String sender;
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

    @Getter
    @Setter
    public static class Features {

        private final List<Feature> enabled = new ArrayList<>();
        private @Pattern(regexp = EMAIL_PATTERN) String scoresManagerEmail;

        public boolean hasFeature(final Feature feature) {
            return enabled.contains(feature);
        }

        public List<Feature> getEnabled() {
            return Collections.unmodifiableList(this.enabled);
        }
    }
}
