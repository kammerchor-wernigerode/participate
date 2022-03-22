package de.vinado.wicket.participate;

import de.agilecoders.wicket.core.settings.IBootstrapSettings;
import de.vinado.wicket.bt4.AuthenticatedBootstrapWebApplication;
import de.vinado.wicket.participate.configuration.CryptoProperties;
import de.vinado.wicket.participate.ui.event.EventsPage;
import de.vinado.wicket.participate.ui.login.SignInPage;
import de.vinado.wicket.participate.ui.pages.ManagementPageRegistry;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.wicket.authentication.strategy.DefaultAuthenticationStrategy;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.settings.SecuritySettings;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.crypt.SunJceCrypt;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * @author Vincent Nadoll
 */
@Component
@RequiredArgsConstructor
@EnableConfigurationProperties({CryptoProperties.class})
public class ManagementApplication extends AuthenticatedBootstrapWebApplication implements ApplicationContextAware {

    private final CryptoProperties cryptoProperties;

    @Setter
    private ApplicationContext applicationContext;

    @Override
    protected void configureSecurity(SecuritySettings securitySettings) {
        super.configureSecurity(securitySettings);

        SunJceCrypt crypt = new SunJceCrypt(cryptoProperties.getPbeSalt().getBytes(StandardCharsets.UTF_8),
            cryptoProperties.getPbeIterationCount());
        crypt.setKey(cryptoProperties.getSessionSecret());
        DefaultAuthenticationStrategy authenticationStrategy = new DefaultAuthenticationStrategy("_login", crypt);
        securitySettings.setAuthenticationStrategy(authenticationStrategy);
    }

    @Override
    protected void mountPages() {
        ManagementPageRegistry.getInstance().mountPages(this);
    }

    @Override
    protected void installSpringComponentScanning() {
        getComponentInstantiationListeners().add(new SpringComponentInjector(this, applicationContext));
    }

    @Override
    protected void customizeBoostrap(IBootstrapSettings settings) {
        super.customizeBoostrap(settings);
        settings.setJsResourceFilterName("footer-container");
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