package de.vinado.wicket.participate.configuration;

import de.vinado.wicket.participate.ui.pages.ManagementPageRegistry;
import org.apache.wicket.protocol.http.WicketFilter;
import org.apache.wicket.spring.SpringWebApplicationFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.ContextCleanupListener;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.EnumSet;
import java.util.stream.Stream;

/**
 * Wicket request handling configuration for Servlet 3.0+ and replacement for web.xml.
 */
@Configuration
public class WebInitializer implements ServletContextInitializer {

    private final ApplicationProperties properties;

    @Autowired
    public WebInitializer(final ApplicationProperties properties) {
        this.properties = properties;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        final FilterRegistration filter = servletContext.addFilter("wicket.participate.management", WicketFilter.class);
        filter.setInitParameter(WicketFilter.APP_FACT_PARAM, SpringWebApplicationFactory.class.getName());
        filter.setInitParameter("applicationClassName", "de.vinado.wicket.participate.ManagementApplication");
        filter.setInitParameter("applicationBean", "managementApplication");
        filter.setInitParameter(WicketFilter.IGNORE_PATHS_PARAM, "/static");
        filter.setInitParameter(WicketFilter.FILTER_MAPPING_PARAM, "/*");
        filter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST, DispatcherType.ERROR), false, Stream.concat(Stream.of("/", "/wicket/*"), ManagementPageRegistry.getInstance().getPaths()).toArray(String[]::new));
        if (properties.isDevelopmentMode()) {
            filter.setInitParameter("configuration", "development");
        } else {
            filter.setInitParameter("configuration", "deployment");
        }

        servletContext.addListener(new ContextCleanupListener());
    }
}
