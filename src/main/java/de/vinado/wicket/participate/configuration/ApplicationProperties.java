package de.vinado.wicket.participate.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.net.MalformedURLException;
import java.net.URL;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Configuration
@ConfigurationProperties("app")
@Getter
@Setter
public class ApplicationProperties implements InitializingBean {

    private static final String EMAIL_PATTERN = "^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,6}$";

    private String version;
    private @NotBlank String baseUrl;
    private String customer = "";
    private @NotBlank String participatePassword;
    private Mail mail;
    private int deadlineOffset = -1;
    private String organizationResponsible;
    private String sleepingPlaceResponsible;

    @Override
    public void afterPropertiesSet() throws Exception {
        checkBaseUrl();
    }

    private void checkBaseUrl() throws MalformedURLException {
        new URL(this.baseUrl);
    }

    @Getter
    @Setter
    public static class Mail {

        private @Pattern(regexp = EMAIL_PATTERN) String sender;
        private String footer = "Participate";
        private @Pattern(regexp = EMAIL_PATTERN) String replyTo;
    }
}
