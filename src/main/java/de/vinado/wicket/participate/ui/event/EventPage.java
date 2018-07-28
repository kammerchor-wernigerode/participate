package de.vinado.wicket.participate.ui.event;

import de.agilecoders.wicket.core.markup.html.bootstrap.navigation.Breadcrumb;
import de.vinado.wicket.participate.ui.pages.ParticipatePage;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * Base page for the application.
 */
public class EventPage extends ParticipatePage {

    public EventPage() {
        this(new PageParameters());
    }

    /**
     * @param parameters {@link org.apache.wicket.request.mapper.parameter.PageParameters}
     */
    public EventPage(final PageParameters parameters) {
        super(parameters);

        final Breadcrumb breadcrumb = new Breadcrumb("breadcrumb");
        breadcrumb.setOutputMarkupPlaceholderTag(true);
        breadcrumb.setVisible(false);
        add(breadcrumb);

        final BreadCrumbPanel breadCrumbPanel = new EventMainPanel("eventPanel", breadcrumb);
        breadCrumbPanel.setOutputMarkupId(true);
        breadcrumb.setActive(breadCrumbPanel);
        add(breadCrumbPanel);
    }
}
