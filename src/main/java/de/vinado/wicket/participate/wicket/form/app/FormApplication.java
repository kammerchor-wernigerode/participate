package de.vinado.wicket.participate.wicket.form.app;

import de.agilecoders.wicket.core.settings.IBootstrapSettings;
import de.vinado.wicket.bt4.AuthenticatedBootstrapWebApplication;
import de.vinado.wicket.participate.wicket.form.ui.FormPage;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.wicket.Page;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.authroles.authentication.pages.SignInPage;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author Vincent Nadoll
 */
@Component
@RequiredArgsConstructor
public class FormApplication extends AuthenticatedBootstrapWebApplication implements ApplicationContextAware {

    @Setter
    private ApplicationContext applicationContext;

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
        return SignInPage.class;
    }

    @Override
    public Class<? extends Page> getHomePage() {
        return FormPage.class;
    }
}
