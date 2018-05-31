package de.vinado.wicket.participate.component.link;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapLink;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbParticipant;
import org.apache.wicket.model.Model;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public abstract class BootstrapBreadcrumbLink extends BootstrapLink<IBreadCrumbModel> {

    private final IBreadCrumbModel breadCrumbModel;

    public BootstrapBreadcrumbLink(final String id, final IBreadCrumbModel breadCrumbModel) {
        super(id, Model.of(breadCrumbModel));
        this.breadCrumbModel = breadCrumbModel;
    }

    @Override
    public void onClick() {
        final IBreadCrumbParticipant active = breadCrumbModel.getActive();
        if (active == null) {
            throw new IllegalStateException("The model has no active bread crumb. Before using " +
                    this + ", you have to have at least one bread crumb in the model");
        }

        final IBreadCrumbParticipant participant = getParticipant(active.getComponent().getId());

        addStateChange();

        breadCrumbModel.setActive(participant);
    }

    protected abstract IBreadCrumbParticipant getParticipant(String componentId);
}
