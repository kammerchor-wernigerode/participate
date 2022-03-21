package de.vinado.wicket.participate;

import com.google.javascript.jscomp.CompilationLevel;
import de.agilecoders.wicket.core.Bootstrap;
import de.agilecoders.wicket.core.markup.html.RenderJavaScriptToFooterHeaderResponseDecorator;
import de.agilecoders.wicket.core.request.resource.caching.version.Adler32ResourceVersion;
import de.agilecoders.wicket.core.settings.BootstrapSettings;
import de.agilecoders.wicket.core.settings.IBootstrapSettings;
import de.agilecoders.wicket.extensions.javascript.GoogleClosureJavaScriptCompressor;
import de.agilecoders.wicket.extensions.javascript.YuiCssCompressor;
import de.vinado.wicket.participate.configuration.CryptoProperties;
import de.vinado.wicket.participate.ui.event.EventsPage;
import de.vinado.wicket.participate.ui.login.SignInPage;
import de.vinado.wicket.participate.ui.pages.ErrorPage;
import de.vinado.wicket.participate.ui.pages.ExpiredPage;
import de.vinado.wicket.participate.ui.pages.PageRegistry;
import lombok.RequiredArgsConstructor;
import org.apache.wicket.RuntimeConfigurationType;
import org.apache.wicket.authentication.strategy.DefaultAuthenticationStrategy;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.AuthenticatedWebApplication;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AnnotationsRoleAuthorizationStrategy;
import org.apache.wicket.core.request.handler.PageProvider;
import org.apache.wicket.core.request.handler.RenderPageRequestHandler;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.https.HttpsConfig;
import org.apache.wicket.protocol.https.HttpsMapper;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.cycle.IRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.resource.caching.FilenameWithVersionResourceCachingStrategy;
import org.apache.wicket.request.resource.caching.NoOpResourceCachingStrategy;
import org.apache.wicket.request.resource.caching.version.CachingResourceVersion;
import org.apache.wicket.serialize.java.DeflatedJavaSerializer;
import org.apache.wicket.settings.RequestCycleSettings;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.crypt.SunJceCrypt;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
@EnableConfigurationProperties({CryptoProperties.class})
public class ManagementApplication extends AuthenticatedWebApplication {

    private final CryptoProperties cryptoProperties;

    @Override
    protected void internalInit() {
        super.internalInit();

        getApplicationSettings().setPageExpiredErrorPage(ExpiredPage.class);
        getApplicationSettings().setInternalErrorPage(ErrorPage.class);
    }

    @Override
    public void init() {
        super.init();

        getCspSettings().blocking().disabled();

        getApplicationSettings().setUploadProgressUpdatesEnabled(true);
        getMarkupSettings().setDefaultMarkupEncoding("UTF-8");
        getSecuritySettings().setAuthorizationStrategy(new AnnotationsRoleAuthorizationStrategy(
            roles -> ParticipateSession.get().getRoles().hasAnyRole(roles)));
        SunJceCrypt crypt = new SunJceCrypt(cryptoProperties.getPbeSalt().getBytes(StandardCharsets.UTF_8), cryptoProperties.getPbeIterationCount());
        crypt.setKey(cryptoProperties.getSessionSecret());
        DefaultAuthenticationStrategy authenticationStrategy = new DefaultAuthenticationStrategy("_login", crypt);
        getSecuritySettings().setAuthenticationStrategy(authenticationStrategy);
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
        WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
        getComponentInstantiationListeners().add(new SpringComponentInjector(this, ctx));

        // Error Page
        getRequestCycleListeners().add(new IRequestCycleListener() {
            @Override
            public IRequestHandler onException(final RequestCycle cycle, final Exception ex) {
                return new RenderPageRequestHandler(new PageProvider(new ErrorPage(ex)));
            }
        });
    }

    private void configureBootstrap() {
        final IBootstrapSettings settings = new BootstrapSettings();
        settings.setJsResourceFilterName("footer-container");
        Bootstrap.builder().withBootstrapSettings(settings).install(this);
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
