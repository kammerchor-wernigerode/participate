package de.vinado.wicket.participate.configuration;

import de.vinado.wicket.participate.ParticipateApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Configuration;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
@Configuration
public class WarInitializer extends SpringBootServletInitializer {

    /**
     * @param application
     * @return
     * @inheritDoc
     */
    @Override
    protected SpringApplicationBuilder configure(final SpringApplicationBuilder application) {
        return application.sources(ParticipateApplication.class);
    }
}
