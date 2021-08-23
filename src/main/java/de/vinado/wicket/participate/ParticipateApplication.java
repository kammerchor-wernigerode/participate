package de.vinado.wicket.participate;

import com.google.javascript.jscomp.CompilationLevel;
import de.agilecoders.wicket.core.Bootstrap;
import de.agilecoders.wicket.core.markup.html.RenderJavaScriptToFooterHeaderResponseDecorator;
import de.agilecoders.wicket.core.request.resource.caching.version.Adler32ResourceVersion;
import de.agilecoders.wicket.core.settings.BootstrapSettings;
import de.agilecoders.wicket.core.settings.IBootstrapSettings;
import de.agilecoders.wicket.extensions.javascript.GoogleClosureJavaScriptCompressor;
import de.agilecoders.wicket.extensions.javascript.YuiCssCompressor;
import de.agilecoders.wicket.less.BootstrapLess;
import de.vinado.wicket.participate.configuration.ApplicationProperties;
import de.vinado.wicket.participate.ui.administration.AdminPage;
import de.vinado.wicket.participate.ui.event.EventsPage;
import de.vinado.wicket.participate.ui.form.FormPage;
import de.vinado.wicket.participate.ui.form.FormSignInPage;
import de.vinado.wicket.participate.ui.login.SignInPage;
import de.vinado.wicket.participate.ui.pages.ErrorPage;
import de.vinado.wicket.participate.ui.pages.ExpiredPage;
import de.vinado.wicket.participate.ui.pages.PageNotFoundPage;
import de.vinado.wicket.participate.ui.pages.PageRegistry;
import de.vinado.wicket.participate.ui.resetPassword.ResetPasswordPage;
import de.vinado.wicket.participate.ui.singers.SingersPage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.wicket.Application;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.authentication.strategy.DefaultAuthenticationStrategy;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AnnotationsRoleAuthorizationStrategy;
import org.apache.wicket.core.request.handler.PageProvider;
import org.apache.wicket.core.request.handler.RenderPageRequestHandler;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.RequestUtils;
import org.apache.wicket.protocol.https.HttpsConfig;
import org.apache.wicket.protocol.https.HttpsMapper;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.UrlRenderer;
import org.apache.wicket.request.cycle.AbstractRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.resource.caching.FilenameWithVersionResourceCachingStrategy;
import org.apache.wicket.request.resource.caching.NoOpResourceCachingStrategy;
import org.apache.wicket.request.resource.caching.version.CachingResourceVersion;
import org.apache.wicket.serialize.java.DeflatedJavaSerializer;
import org.apache.wicket.settings.RequestCycleSettings;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.servlet.http.HttpServletRequest;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties
@Getter
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ParticipateApplication extends AuthenticatedWebApplication {

    private final ApplicationProperties applicationProperties;

    @Value("${spring.application.name}")
    private String applicationName;

    /**
     * Runs a {@link org.springframework.boot.SpringApplication} using default settings.
     *
     * @param args the application arguments
     * @throws Exception exception if the application cannot be started
     */
    public static void main(final String[] args) throws Exception {
        SpringApplication.run(ParticipateApplication.class, args);
    }

    public static ParticipateApplication get() {
        return (ParticipateApplication) Application.get();
    }

    public ApplicationProperties getApplicationProperties() {
        return applicationProperties;
    }

    public boolean isInDevelopmentMode() {
        return applicationProperties.isDevelopmentMode();
    }

    public String getBaseUrl() {
        final HttpServletRequest req = (HttpServletRequest) (RequestCycle.get().getRequest()).getContainerRequest();
        return RequestUtils.toAbsolutePath(req.getRequestURL().toString(), "");
    }

    public Url getRequestedUrl() {
        return new UrlRenderer(RequestCycle.get().getRequest()).getBaseUrl();
    }

    @Override
    protected void internalInit() {
        super.internalInit();

        getApplicationSettings().setPageExpiredErrorPage(ExpiredPage.class);
        getApplicationSettings().setInternalErrorPage(ErrorPage.class);
    }

    @Override
    public void init() {
        super.init();

        getApplicationSettings().setUploadProgressUpdatesEnabled(true);
        getMarkupSettings().setDefaultMarkupEncoding("UTF-8");
        getSecuritySettings().setAuthorizationStrategy(new AnnotationsRoleAuthorizationStrategy(
            roles -> ParticipateSession.get().getRoles().hasAnyRole(roles)));
        getSecuritySettings().setAuthenticationStrategy(new DefaultAuthenticationStrategy("_login", "fmGr-36Fsh_ds3hU6"));
        setRootRequestMapper(new HttpsMapper(getRootRequestMapper(), new HttpsConfig(80, 443)));
        getMarkupSettings().setStripWicketTags(true);

        PageRegistry.getInstance()
            .forEach(registration -> mountPage(registration.getPath(), registration.getPageClass()));

        // Install Bootstrap
        configureBootstrap();
        optimizeForWebPerformance();

        // Deployment vs. Development Mode
        if (RuntimeConfigurationType.DEPLOYMENT.equals(getConfigurationType())) {
            getResourceSettings().setResourcePollFrequency(null);
            getDebugSettings().setComponentUseCheck(false);
            getMarkupSettings().setCompressWhitespace(true);
            getMarkupSettings().setStripComments(true);
        } else if (RuntimeConfigurationType.DEVELOPMENT.equals(getConfigurationType())) {
            getDebugSettings().setComponentPathAttributeName("data-wicket-path");
            getDebugSettings().setOutputMarkupContainerClassName(true);
        }

        // spring injector for @SpringBean annotations
        getComponentInstantiationListeners().add(new SpringComponentInjector(this));

        // Error Page
        getRequestCycleListeners().add(new AbstractRequestCycleListener() {
            @Override
            public IRequestHandler onException(final RequestCycle cycle, final Exception ex) {
                return new RenderPageRequestHandler(new PageProvider(new ErrorPage(ex)));
            }
        });
    }

    @Override
    protected void validateInit() {
        super.validateInit();
    }

    private void configureBootstrap() {
        final IBootstrapSettings settings = new BootstrapSettings();
        Bootstrap.builder().withBootstrapSettings(settings).install(this);

        settings.setJsResourceFilterName("footer-container");

        BootstrapLess.install(this);
    }

    private void optimizeForWebPerformance() {
        if (usesDeploymentConfig()) {
            getResourceSettings().setCachingStrategy(new FilenameWithVersionResourceCachingStrategy(
                "-v-",
                new CachingResourceVersion(new Adler32ResourceVersion())
            ));

            getResourceSettings().setJavaScriptCompressor(new GoogleClosureJavaScriptCompressor(CompilationLevel.SIMPLE_OPTIMIZATIONS));
            getResourceSettings().setCssCompressor(new YuiCssCompressor());

            getFrameworkSettings().setSerializer(new DeflatedJavaSerializer(getApplicationKey()));
        } else {
            getResourceSettings().setCachingStrategy(new NoOpResourceCachingStrategy());
        }

        setHeaderResponseDecorator(new RenderJavaScriptToFooterHeaderResponseDecorator());
        getRequestCycleSettings().setRenderStrategy(RequestCycleSettings.RenderStrategy.ONE_PASS_RENDER);
    }

    @Override
    public Class<? extends WebPage> getHomePage() {
        return EventsPage.class;
    }

    @Override
    protected Class<? extends AbstractAuthenticatedWebSession> getWebSessionClass() {
        return ParticipateSession.class;
    }

    @Override
    protected Class<? extends WebPage> getSignInPageClass() {
        return SignInPage.class;
    }
}
