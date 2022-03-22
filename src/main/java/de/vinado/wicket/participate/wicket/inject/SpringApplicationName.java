package de.vinado.wicket.participate.wicket.inject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Vincent Nadoll
 */
@Component
public class SpringApplicationName implements ApplicationName {

    @Value("${spring.application.name}")
    private String applicationName;

    @Override
    public String get() {
        return applicationName;
    }
}
