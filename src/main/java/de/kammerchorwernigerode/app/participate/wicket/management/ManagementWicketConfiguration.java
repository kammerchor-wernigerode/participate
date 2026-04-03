package de.kammerchorwernigerode.app.participate.wicket.management;

import de.kammerchorwernigerode.app.participate.wicket.WicketProperties;
import org.apache.wicket.protocol.http.WicketFilter;
import org.apache.wicket.spring.SpringWebApplicationFactory;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

import java.util.EnumSet;
import jakarta.servlet.DispatcherType;

import lombok.RequiredArgsConstructor;
import lombok.Setter;

import static jakarta.servlet.DispatcherType.REQUEST;
import static org.apache.wicket.protocol.http.WicketFilter.APP_FACT_PARAM;
import static org.apache.wicket.protocol.http.WicketFilter.FILTER_MAPPING_PARAM;
import static org.apache.wicket.protocol.http.WicketFilter.IGNORE_PATHS_PARAM;

@Configuration
@RequiredArgsConstructor
class ManagementWicketConfiguration implements ApplicationContextAware, EnvironmentAware {

    public static final String APP_ROOT = "";

    private static final String RUNTIME_CONFIGURATION_PARAM = "configuration";
    private static final EnumSet<DispatcherType> DISPATCHER_TYPES = EnumSet.of(REQUEST);
    private static final String[] IGNORED_PATHS = {
        "/favicon.ico",
        "/robots.txt",
        "/static",
    };

    @Setter
    private ApplicationContext applicationContext;

    @Setter
    private Environment environment;

    @Bean
    @ConfigurationProperties("wicket.management")
    public WicketProperties managementWicketProperties() {
        return new WicketProperties();
    }

    @Bean
    public ManagementWicketApplication managementWicketApplication() {
        ManagementWicketApplication application = new ManagementWicketApplication(environment);
        SpringComponentInjector springComponentInjector = new SpringComponentInjector(application, applicationContext);
        application.getComponentInstantiationListeners().add(springComponentInjector);
        return application;
    }

    @Bean
    public FilterRegistrationBean<WicketFilter> managementWicketFilterRegistration() {
        WicketProperties wicketProperties = managementWicketProperties();
        WicketFilter filter = new WicketFilter();
        FilterRegistrationBean<WicketFilter> registration = new FilterRegistrationBean<>(filter);
        registration.setName("wicket.management");
        registration.addInitParameter(APP_FACT_PARAM, SpringWebApplicationFactory.class.getName());
        registration.addInitParameter(IGNORE_PATHS_PARAM, String.join(",", IGNORED_PATHS));
        registration.addInitParameter(FILTER_MAPPING_PARAM, APP_ROOT + "/*");
        registration.addInitParameter(RUNTIME_CONFIGURATION_PARAM, wicketProperties.getRuntimeConfiguration().name());
        registration.setDispatcherTypes(DISPATCHER_TYPES);
        registration.addUrlPatterns(APP_ROOT + "/*");
        return registration;
    }


    @Profile("oauth2")
    @Configuration
    @RequiredArgsConstructor
    static class WebSecurity {

        private final ClientRegistrationRepository clientRegistrationRepository;

        @Order(Ordered.HIGHEST_PRECEDENCE)
        @Bean
        public SecurityFilterChain managementSecurityFilterChain(HttpSecurity http) {
            OidcClientInitiatedLogoutSuccessHandler frontChannelLogoutHandler =
                new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);
            frontChannelLogoutHandler.setPostLogoutRedirectUri("{baseScheme}://{baseHost}{basePort}" + APP_ROOT);

            http
                .securityMatcher(PathPatternRequestMatcher.withDefaults().matcher("/" + APP_ROOT + "/**"))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                    .anyRequest().authenticated())
                .oauth2Login(Customizer.withDefaults())
                .logout(logout -> logout
                    .logoutSuccessHandler(frontChannelLogoutHandler)
                    .invalidateHttpSession(true)
                    .permitAll())
            ;

            return http.build();
        }
    }
}
