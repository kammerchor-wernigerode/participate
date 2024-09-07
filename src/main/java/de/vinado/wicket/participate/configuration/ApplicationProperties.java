package de.vinado.wicket.participate.configuration;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.net.MalformedURLException;
import java.net.URL;

@Configuration
@ConfigurationProperties("app")
@Getter
@Setter
public class ApplicationProperties implements InitializingBean {

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

        private String footer = "Participate";
    }
}
