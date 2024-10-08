package de.vinado.app.participate.wicket.bt5;

import de.agilecoders.wicket.core.Bootstrap;
import de.agilecoders.wicket.core.markup.html.RenderJavaScriptToFooterHeaderResponseDecorator;
import de.agilecoders.wicket.core.request.resource.caching.version.Adler32ResourceVersion;
import de.agilecoders.wicket.core.settings.BootstrapSettings;
import de.agilecoders.wicket.core.settings.IBootstrapSettings;
import de.agilecoders.wicket.extensions.javascript.YuiCssCompressor;
import de.vinado.app.participate.event.model.EventName;
import de.vinado.app.participate.event.model.Interval;
import de.vinado.app.participate.event.presentation.ui.EventNameConverter;
import de.vinado.app.participate.event.presentation.ui.IntervalConverter;
import de.vinado.wicket.http.HttpError;
import de.vinado.wicket.participate.ui.pages.ErrorPage;
import de.vinado.wicket.participate.ui.pages.ExpiredPage;
import org.apache.wicket.ConverterLocator;
import org.apache.wicket.IConverterLocator;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.authroles.authorization.strategies.role.RoleAuthorizationStrategy;
import org.apache.wicket.authroles.authorization.strategies.role.Roles;
import org.apache.wicket.core.request.handler.PageProvider;
import org.apache.wicket.core.request.handler.RenderPageRequestHandler;
import org.apache.wicket.csp.ContentSecurityPolicySettings;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.cycle.IRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.cycle.RequestCycleListenerCollection;
import org.apache.wicket.request.resource.caching.FilenameWithVersionResourceCachingStrategy;
import org.apache.wicket.request.resource.caching.NoOpResourceCachingStrategy;
import org.apache.wicket.request.resource.caching.version.CachingResourceVersion;
import org.apache.wicket.serialize.java.DeflatedJavaSerializer;
import org.apache.wicket.settings.ApplicationSettings;
import org.apache.wicket.settings.DebugSettings;
import org.apache.wicket.settings.DebugSettings.ClassOutputStrategy;
import org.apache.wicket.settings.MarkupSettings;
import org.apache.wicket.settings.RequestCycleSettings;
import org.apache.wicket.settings.ResourceSettings;
import org.apache.wicket.settings.SecuritySettings;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;

public abstract class AuthenticatedBootstrapWebApplication extends AuthenticatedWebApplication {

    @Override
    protected void init() {
        super.init();

        ApplicationSettings applicationSettings = getApplicationSettings();
        configureApplication(applicationSettings);

        ContentSecurityPolicySettings cspSettings = getCspSettings();
        configureCps(cspSettings);

        MarkupSettings markupSettings = getMarkupSettings();
        configureMarkup(markupSettings);

        SecuritySettings securitySettings = getSecuritySettings();
        configureSecurity(securitySettings);

        ResourceSettings resourceSettings = getResourceSettings();
        configureResourceManagement(resourceSettings);

        mountPages();

        installBootstrap();
        optimize(resourceSettings);

        DebugSettings debugSettings = getDebugSettings();
        configureDebug(debugSettings);

        installSpringComponentScanning();

        RequestCycleListenerCollection listeners = getRequestCycleListeners();
        configureRequestCycleListeners(listeners);
    }

    @Override
    protected IConverterLocator newConverterLocator() {
        ConverterLocator converterLocator = new ConverterLocator();
        converterLocator.set(Interval.class, new IntervalConverter());
        converterLocator.set(EventName.class, new EventNameConverter());
        return converterLocator;
    }

    protected void configureRequestCycleListeners(RequestCycleListenerCollection listeners) {
        listeners.add(new IRequestCycleListener() {
            @Override
            public IRequestHandler onException(RequestCycle cycle, Exception ex) {
                ErrorPage page;
                if (ex instanceof HttpError) {
                    page = new ErrorPage(ex, ((HttpError) ex).getStatus());
                } else {
                    page = new ErrorPage(ex);
                }
                return new RenderPageRequestHandler(new PageProvider(page));
            }
        });
    }

    protected void configureApplication(ApplicationSettings applicationSettings) {
        applicationSettings.setPageExpiredErrorPage(ExpiredPage.class);
        applicationSettings.setInternalErrorPage(ErrorPage.class);
        applicationSettings.setUploadProgressUpdatesEnabled(true);
    }

    protected void configureCps(ContentSecurityPolicySettings cspSettings) {
        cspSettings.blocking().disabled();
    }

    protected void configureMarkup(MarkupSettings markupSettings) {
        markupSettings.setDefaultMarkupEncoding(StandardCharsets.UTF_8.name());
        if (usesDeploymentConfig()) {
            markupSettings.setCompressWhitespace(true);
            markupSettings.setStripComments(true);
        }
    }

    protected void configureSecurity(SecuritySettings securitySettings) {
        RoleAuthorizationStrategy strategy = new RoleAuthorizationStrategy(this::authorized);
        securitySettings.setAuthorizationStrategy(strategy);
    }

    private boolean authorized(Roles roles) {
        return AbstractAuthenticatedWebSession.get()
            .getRoles()
            .hasAnyRole(roles);
    }

    protected void configureResourceManagement(ResourceSettings resourceSettings) {
        if (usesDeploymentConfig()) resourceSettings.setResourcePollFrequency(null);
    }

    protected abstract void mountPages();

    protected void installBootstrap() {
        IBootstrapSettings settings = new BootstrapSettings();
        customizeBoostrap(settings);

        Bootstrap.Builder builder = Bootstrap.builder().withBootstrapSettings(settings);
        customizeBootstrap(builder);

        builder.install(this);
    }

    protected void customizeBoostrap(IBootstrapSettings settings) {
    }

    protected void customizeBootstrap(Bootstrap.Builder builder) {
    }

    protected void optimize(ResourceSettings resourceSettings) {
        if (usesDeploymentConfig()) {
            resourceSettings.setCachingStrategy(new FilenameWithVersionResourceCachingStrategy(
                "-v-",
                new CachingResourceVersion(new Adler32ResourceVersion())
            ));

            resourceSettings.setCssCompressor(new YuiCssCompressor());

            getFrameworkSettings().setSerializer(new DeflatedJavaSerializer(getApplicationKey()));
        } else {
            resourceSettings.setCachingStrategy(new NoOpResourceCachingStrategy());
        }

        if (StringUtils.hasText(Bootstrap.getSettings().getJsResourceFilterName()))
            getHeaderResponseDecorators().replaceAll(new RenderJavaScriptToFooterHeaderResponseDecorator());
        getRequestCycleSettings().setRenderStrategy(RequestCycleSettings.RenderStrategy.ONE_PASS_RENDER);
    }

    private void configureDebug(DebugSettings debugSettings) {
        if (usesDeploymentConfig()) {
            debugSettings.setComponentUseCheck(false);
        } else if (usesDevelopmentConfig()) {
            debugSettings.setComponentPathAttributeName("data-wicket-path");
            debugSettings.setOutputMarkupContainerClassNameStrategy(ClassOutputStrategy.HTML_COMMENT);
        }
    }

    protected void installSpringComponentScanning() {
    }
}
