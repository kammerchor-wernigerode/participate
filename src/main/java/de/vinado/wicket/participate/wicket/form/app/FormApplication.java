package de.vinado.wicket.participate.wicket.form.app;

import de.agilecoders.wicket.core.settings.IBootstrapSettings;
import de.vinado.wicket.bt4.AuthenticatedBootstrapWebApplication;
import de.vinado.wicket.participate.configuration.CryptoProperties;
import de.vinado.wicket.participate.wicket.form.ui.FormPage;
import de.vinado.wicket.participate.wicket.form.ui.FormSignInPage;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.wicket.Page;
import org.apache.wicket.authentication.IAuthenticationStrategy;
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

/**
 * @author Vincent Nadoll
 */
@Component
@RequiredArgsConstructor
@EnableConfigurationProperties({CryptoProperties.class})
public class FormApplication extends AuthenticatedBootstrapWebApplication implements ApplicationContextAware {

    @Setter
    private ApplicationContext applicationContext;

    private final CryptoProperties cryptoProperties;

    @Override
    protected void configureSecurity(SecuritySettings securitySettings) {
        super.configureSecurity(securitySettings);

        SunJceCrypt crypt = new SunJceCrypt(cryptoProperties.getPbeSalt().getBytes(StandardCharsets.UTF_8),
            cryptoProperties.getPbeIterationCount());
        crypt.setKey(cryptoProperties.getSessionSecret());
        IAuthenticationStrategy authenticationStrategy = new DefaultAuthenticationStrategy("_form-login", crypt);
        securitySettings.setAuthenticationStrategy(authenticationStrategy);
    }

    @Override
    protected void mountPages() {
        FormPageRegistry.instance().mountPages(this);
    }

    @Override
    protected void customizeBoostrap(IBootstrapSettings settings) {
        settings.setJsResourceFilterName("footer-container");
    }

    @Override
    protected void installSpringComponentScanning() {
        SpringComponentInjector injector = new SpringComponentInjector(this, applicationContext);
        getComponentInstantiationListeners().add(injector);
    }

    @Override
    protected Class<? extends AbstractAuthenticatedWebSession> getWebSessionClass() {
        return FormSession.class;
    }

    @Override
    protected Class<? extends WebPage> getSignInPageClass() {
        return FormSignInPage.class;
    }

    @Override
    public Class<? extends Page> getHomePage() {
        return FormPage.class;
    }
}
