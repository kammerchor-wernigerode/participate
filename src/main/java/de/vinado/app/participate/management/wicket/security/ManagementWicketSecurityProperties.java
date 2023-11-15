package de.vinado.app.participate.management.wicket.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("app.wicket.management.security")
public class ManagementWicketSecurityProperties {

    /**
     * Username to impersonate when using the management application.
     */
    private String impersonateUsername;
}
