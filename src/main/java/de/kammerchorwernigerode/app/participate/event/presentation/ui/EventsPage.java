package de.kammerchorwernigerode.app.participate.event.presentation.ui;

import de.kammerchorwernigerode.app.participate.wicket.bootstrap.BootstrapPage;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

public class EventsPage extends BootstrapPage {

    @Override
    protected IModel<?> titleModel() {
        return new ResourceModel("events");
    }
}
