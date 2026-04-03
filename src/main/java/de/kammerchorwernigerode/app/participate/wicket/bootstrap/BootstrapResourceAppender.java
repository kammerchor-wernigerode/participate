package de.kammerchorwernigerode.app.participate.wicket.bootstrap;

import org.apache.wicket.Component;
import org.apache.wicket.application.IComponentInitializationListener;

public class BootstrapResourceAppender implements IComponentInitializationListener {

    @Override
    public void onInitialize(Component component) {
        if (component instanceof BootstrapPage page) {
            page.add(BootstrapBehavior.getInstance());
        }
    }
}
