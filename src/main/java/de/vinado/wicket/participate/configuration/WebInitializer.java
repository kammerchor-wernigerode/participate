package de.vinado.wicket.participate.configuration;

import de.vinado.wicket.participate.ui.pages.ManagementPageRegistry;
import lombok.RequiredArgsConstructor;
import org.apache.wicket.protocol.http.WicketFilter;
import org.apache.wicket.spring.SpringWebApplicationFactory;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.ContextCleanupListener;

import java.util.EnumSet;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;

/**
 * Wicket request handling configuration for Servlet 3.0+ and replacement for web.xml.
 */
@Configuration
@RequiredArgsConstructor
public class WebInitializer implements ServletContextInitializer {

    static final String APP_ROOT = "/_";

    private final ApplicationProperties properties;

    @Override
    public void onStartup(ServletContext servletContext) {
        FilterRegistration filter = servletContext.addFilter("wicket.participate.management", WicketFilter.class);
        filter.setInitParameter(WicketFilter.APP_FACT_PARAM, SpringWebApplicationFactory.class.getName());
        filter.setInitParameter("applicationClassName", "de.vinado.wicket.participate.ManagementApplication");
        filter.setInitParameter("applicationBean", "managementApplication");
        filter.setInitParameter(WicketFilter.IGNORE_PATHS_PARAM, "/static");
        filter.setInitParameter(WicketFilter.FILTER_MAPPING_PARAM, APP_ROOT + "/*");
        filter.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST, DispatcherType.ERROR), false, getUrlPatterns());
        filter.setInitParameter("configuration", properties.isDevelopmentMode() ? "development" : "deployment");
        servletContext.addListener(new ContextCleanupListener());
    }

    private String[] getUrlPatterns() {
        return Stream.concat(listResourceRoots(), listPagePaths())
            .map(prepend(APP_ROOT))
            .toArray(String[]::new);
    }

    private Stream<String> listResourceRoots() {
        return Stream.of("/wicket/*");
    }

    private Stream<String> listPagePaths() {
        return ManagementPageRegistry.getInstance().getPaths()
            .map(path -> path.replaceAll("#\\{.+\\}", "*"));
    }

    private static Function<String, String> prepend(String value) {
        return path -> value + path;
    }
}
