package de.kammerchorwernigerode.app.participate.wicket.markup.html.pages;

import de.kammerchorwernigerode.app.participate.wicket.bootstrap.BootstrapPage;

public abstract class AbstractErrorPage extends BootstrapPage {

    @Override
    public boolean isErrorPage() {
        return true;
    }

    @Override
    public boolean isVersioned() {
        return false;
    }
}
