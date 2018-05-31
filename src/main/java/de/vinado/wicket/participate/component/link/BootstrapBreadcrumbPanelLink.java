package de.vinado.wicket.participate.component.link;

import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbParticipant;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanel;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanelFactory;
import org.apache.wicket.extensions.breadcrumb.panel.IBreadCrumbPanelFactory;
import org.apache.wicket.util.lang.Args;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class BootstrapBreadcrumbPanelLink extends BootstrapBreadcrumbLink {

    private final IBreadCrumbModel breadCrumbModel;

    private final IBreadCrumbPanelFactory breadCrumbPanelFactory;

    public BootstrapBreadcrumbPanelLink(final String id, final BreadCrumbPanel caller,
                                        final Class<? extends BreadCrumbPanel> panelClass) {
        this(id, caller.getBreadCrumbModel(), new BreadCrumbPanelFactory(panelClass));
    }

    public BootstrapBreadcrumbPanelLink(final String id, final IBreadCrumbModel breadCrumbModel,
                                        final Class<? extends BreadCrumbPanel> panelClass) {
        this(id, breadCrumbModel, new BreadCrumbPanelFactory(panelClass));
    }

    public BootstrapBreadcrumbPanelLink(final String id, final IBreadCrumbModel breadCrumbModel,
                                        final IBreadCrumbPanelFactory breadCrumbPanelFactory) {
        super(id, breadCrumbModel);

        Args.notNull(breadCrumbModel, "breadCrumbModel");
        Args.notNull(breadCrumbPanelFactory, "breadCrumbPanelFactory");

        this.breadCrumbModel = breadCrumbModel;
        this.breadCrumbPanelFactory = breadCrumbPanelFactory;
    }

    @Override
    protected final IBreadCrumbParticipant getParticipant(final String componentId) {
        return breadCrumbPanelFactory.create(componentId, breadCrumbModel);
    }
}
