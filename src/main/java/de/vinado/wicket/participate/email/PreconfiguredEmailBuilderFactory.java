package de.vinado.wicket.participate.email;

import de.vinado.wicket.participate.configuration.ApplicationProperties;
import org.springframework.stereotype.Component;

@Component
public class PreconfiguredEmailBuilderFactory implements EmailBuilderFactory {

    private final ApplicationProperties properties;
    private final ApplicationProperties.Mail mailProperties;

    public PreconfiguredEmailBuilderFactory(ApplicationProperties properties) {
        this.properties = properties;
        this.mailProperties = properties.getMail();
    }

    @Override
    public Email.Builder create() {
        return Email.builder(mailProperties.getSender(), properties.getCustomer())
            .data("baseUrl", properties.getBaseUrl())
            .data("footer", mailProperties.getFooter())
            ;
    }
}
