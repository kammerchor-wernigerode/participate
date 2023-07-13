package de.vinado.wicket.test.bootstrap;

import de.agilecoders.wicket.core.Bootstrap;
import org.apache.wicket.mock.MockApplication;

public class BootstrapApplication extends MockApplication {

    @Override
    protected void init() {
        super.init();

        Bootstrap.install(this);
    }
}
