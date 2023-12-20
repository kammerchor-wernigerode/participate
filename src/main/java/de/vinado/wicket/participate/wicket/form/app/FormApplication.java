package de.vinado.wicket.participate.wicket.form.app;

import de.agilecoders.wicket.core.settings.IBootstrapSettings;
import de.vinado.app.participate.wicket.bt5.AuthenticatedBootstrapWebApplication;
import de.vinado.app.participate.wicket.crypto.CryptFactory;
import de.vinado.wicket.participate.wicket.form.ui.FormPage;
import de.vinado.wicket.participate.wicket.form.ui.FormSignInPage;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.wicket.Page;
import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.authentication.IAuthenticationStrategy;
import org.apache.wicket.authentication.strategy.DefaultAuthenticationStrategy;
import org.apache.wicket.authorization.IAuthorizationStrategy;
import org.apache.wicket.authorization.IUnauthorizedComponentInstantiationListener;
import org.apache.wicket.authorization.UnauthorizedInstantiationException;
import org.apache.wicket.authorization.strategies.page.AbstractPageAuthorizationStrategy;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.IRequestParameters;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.cycle.IRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.cycle.RequestCycleListenerCollection;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.settings.SecuritySettings;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.crypt.ICrypt;
import org.apache.wicket.util.string.StringValue;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.apache.wicket.request.mapper.parameter.INamedParameters.Type.QUERY_STRING;

@Component
@RequiredArgsConstructor
public class FormApplication extends AuthenticatedBootstrapWebApplication implements ApplicationContextAware {

    @Setter
    private ApplicationContext applicationContext;

    private final CryptFactory cryptFactory;

    @Override
    protected void configureRequestCycleListeners(RequestCycleListenerCollection listeners) {
        super.configureRequestCycleListeners(listeners);
        TokenExtractingRequestCycleListener requestCycleListener = new TokenExtractingRequestCycleListener(FormSession::get);
        getRequestCycleListeners().add(requestCycleListener);
    }

    @Override
    protected void configureSecurity(SecuritySettings securitySettings) {
        IAuthorizationStrategy strategy = new HomePageAuthorizationStrategy(FormSession::get);
        IUnauthorizedComponentInstantiationListener listener = new SigningOutUnauthorizedComponentInstantiationListener(FormSession::get);
        securitySettings.setAuthorizationStrategy(strategy);
        securitySettings.setUnauthorizedComponentInstantiationListener(listener);

        ICrypt crypt = cryptFactory.create();
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


    @RequiredArgsConstructor
    private static final class TokenExtractingRequestCycleListener implements IRequestCycleListener {

        private final Supplier<FormSession> sessionSupplier;

        @Override
        public void onBeginRequest(RequestCycle cycle) {
            Optional.of(cycle)
                .map(RequestCycle::getRequest)
                .map(Request::getQueryParameters)
                .map(extract("token"))
                .map(StringValue::toOptionalString)
                .ifPresent(sessionSupplier.get()::setToken);
        }

        private static Function<IRequestParameters, StringValue> extract(String name) {
            return parameters -> parameters.getParameterValue(name);
        }
    }

    @RequiredArgsConstructor
    private final class HomePageAuthorizationStrategy extends AbstractPageAuthorizationStrategy {

        private final Supplier<FormSession> sessionSupplier;

        @Override
        protected <T extends Page> boolean isPageAuthorized(Class<T> pageClass) {
            if (instanceOf(pageClass, getHomePage())) {
                return sessionSupplier.get().authorize();
            }

            return true;
        }
    }

    @RequiredArgsConstructor
    private final class SigningOutUnauthorizedComponentInstantiationListener implements IUnauthorizedComponentInstantiationListener {

        private final Supplier<FormSession> sessionSupplier;

        @Override
        public void onUnauthorizedInstantiation(org.apache.wicket.Component component) {
            if (component instanceof Page) {
                PageParameters pageParameters = new PageParameters();
                pageParameters.add("token", sessionSupplier.get().getToken(), QUERY_STRING);
                sessionSupplier.get().signOut();
                throw new RestartResponseAtInterceptPageException(getSignInPageClass(), pageParameters);
            } else {
                sessionSupplier.get().signOut();
                throw new UnauthorizedInstantiationException(component.getClass());
            }
        }
    }
}
