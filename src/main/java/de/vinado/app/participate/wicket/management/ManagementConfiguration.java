package de.vinado.app.participate.wicket.management;

import de.vinado.app.participate.wicket.WicketProperties;
import de.vinado.wicket.participate.ui.pages.ManagementPageRegistry;
import lombok.RequiredArgsConstructor;
import org.apache.wicket.protocol.http.WicketFilter;
import org.apache.wicket.spring.SpringWebApplicationFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.ContextCleanupListener;

import java.util.EnumSet;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;

import static javax.servlet.DispatcherType.*;

/**
 * Wicket request handling configuration for Servlet 3.0+ and replacement for web.xml.
 */
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(WicketProperties.class)
public class ManagementConfiguration implements ServletContextInitializer {

    static final String APP_ROOT = "/_";

    private final WicketProperties properties;

    @Override
    public void onStartup(ServletContext servletContext) {
        FilterRegistration filter = servletContext.addFilter("wicket.participate.management", WicketFilter.class);
        filter.setInitParameter(WicketFilter.APP_FACT_PARAM, SpringWebApplicationFactory.class.getName());
        filter.setInitParameter("applicationClassName", "de.vinado.wicket.participate.ManagementApplication");
        filter.setInitParameter("applicationBean", "managementApplication");
        filter.setInitParameter(WicketFilter.IGNORE_PATHS_PARAM, "/static");
        filter.setInitParameter(WicketFilter.FILTER_MAPPING_PARAM, APP_ROOT + "/*");
        filter.addMappingForUrlPatterns(EnumSet.of(REQUEST, INCLUDE), false, getUrlPatterns());
        filter.setInitParameter("configuration", properties.getRuntimeConfiguration().name());
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
