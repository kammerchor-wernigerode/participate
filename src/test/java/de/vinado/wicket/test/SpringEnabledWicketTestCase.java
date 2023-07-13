package de.vinado.wicket.test;

import de.vinado.wicket.test.bootstrap.BootstrapApplication;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.spring.injection.annot.SpringComponentInjector;
import org.apache.wicket.util.tester.WicketTestCase;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public abstract class SpringEnabledWicketTestCase extends WicketTestCase {

    @Autowired
    private ApplicationContext cxt;

    @Override
    protected final WebApplication newApplication() {
        return withSpringEnabled(newSpringBasedApplication());
    }

    private WebApplication withSpringEnabled(WebApplication app) {
        app.getComponentInstantiationListeners().add(new SpringComponentInjector(app, cxt));
        return app;
    }

    protected WebApplication newSpringBasedApplication() {
        return new BootstrapApplication();
    }
}
