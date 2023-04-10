package de.vinado.wicket.participate.wicket.form.support;

import de.vinado.wicket.participate.configuration.ApplicationProperties;
import de.vinado.app.participate.wicket.WicketProperties;
import de.vinado.wicket.participate.wicket.form.app.FormPageRegistry;
import lombok.RequiredArgsConstructor;
import org.apache.wicket.protocol.http.WicketFilter;
import org.apache.wicket.spring.SpringWebApplicationFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.ContextCleanupListener;

import java.util.function.Function;
import java.util.stream.Stream;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;

import static java.util.EnumSet.of;
import static java.util.stream.Stream.concat;
import static javax.servlet.DispatcherType.*;
import static org.apache.wicket.protocol.http.WicketFilter.APP_FACT_PARAM;
import static org.apache.wicket.protocol.http.WicketFilter.FILTER_MAPPING_PARAM;
import static org.apache.wicket.protocol.http.WicketFilter.IGNORE_PATHS_PARAM;

/**
 * @author Vincent Nadoll
 */
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(WicketProperties.class)
class FormApplicationServletContextInitializer implements ServletContextInitializer {

    public static final String APP_ROOT = "/form";

    private final ApplicationProperties properties;

    @Override
    public void onStartup(ServletContext servletContext) {
        FilterRegistration filter = servletContext.addFilter("wicket.participate.form", WicketFilter.class);
        filter.setInitParameter(APP_FACT_PARAM, SpringWebApplicationFactory.class.getName());
        filter.setInitParameter("applicationClassName", "de.vinado.wicket.participate.wicket.form.app.FormApplication");
        filter.setInitParameter("applicationBean", "formApplication");
        filter.setInitParameter(IGNORE_PATHS_PARAM, "/static");
        filter.setInitParameter(FILTER_MAPPING_PARAM, APP_ROOT + "/*");
        filter.addMappingForUrlPatterns(of(REQUEST, INCLUDE), false, getUrlPatterns());
        filter.setInitParameter("configuration", properties.isDevelopmentMode() ? "development" : "deployment");
        servletContext.addListener(new ContextCleanupListener());
    }

    private String[] getUrlPatterns() {
        return concat(listResourceRoots(), listPagePaths()).map(prepend(APP_ROOT)).toArray(String[]::new);
    }

    private Stream<String> listResourceRoots() {
        return Stream.of("/wicket/*");
    }

    private Stream<String> listPagePaths() {
        return FormPageRegistry.instance().getPaths();
    }

    private static Function<String, String> prepend(String value) {
        return path -> value + path;
    }
}
