package de.vinado.app.participate.wicket;

import jakarta.servlet.FilterRegistration;
import jakarta.servlet.ServletContext;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WicketFilter;
import org.apache.wicket.spring.SpringWebApplicationFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletContextInitializer;

import java.util.EnumSet;

import static jakarta.servlet.DispatcherType.INCLUDE;
import static jakarta.servlet.DispatcherType.REQUEST;
import static org.apache.commons.lang3.StringUtils.uncapitalize;
import static org.apache.wicket.protocol.http.WicketFilter.APP_FACT_PARAM;
import static org.apache.wicket.protocol.http.WicketFilter.FILTER_MAPPING_PARAM;
import static org.apache.wicket.protocol.http.WicketFilter.IGNORE_PATHS_PARAM;

@RequiredArgsConstructor
@EnableConfigurationProperties(WicketProperties.class)
public abstract class WicketConfiguration implements ServletContextInitializer {

    private final @NonNull String appRoot;
    private final @NonNull String filterName;
    private final @NonNull Class<? extends WebApplication> applicationClass;
    private final @NonNull WicketProperties properties;

    @Override
    public void onStartup(ServletContext servletContext) {
        FilterRegistration filter = addFilter(servletContext);
        initialize(filter);
        addMappingForUrlPatterns(filter);
    }

    protected FilterRegistration addFilter(ServletContext servletContext) {
        return servletContext.addFilter(filterName, WicketFilter.class);
    }

    protected void initialize(FilterRegistration filter) {
        filter.setInitParameter(APP_FACT_PARAM, SpringWebApplicationFactory.class.getName());
        filter.setInitParameter("applicationClassName", applicationClass.getName());
        filter.setInitParameter("applicationBean", uncapitalize(applicationClass.getSimpleName()));
        filter.setInitParameter(IGNORE_PATHS_PARAM, "/static");
        filter.setInitParameter(FILTER_MAPPING_PARAM, appRoot + "/*");
        filter.setInitParameter("configuration", properties.getRuntimeConfiguration().name());
    }

    protected void addMappingForUrlPatterns(FilterRegistration filter) {
        filter.addMappingForUrlPatterns(EnumSet.of(REQUEST, INCLUDE), false, urlPatterns());
    }

    protected abstract String[] urlPatterns();
}
