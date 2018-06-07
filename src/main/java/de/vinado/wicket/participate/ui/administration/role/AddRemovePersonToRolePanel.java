package de.vinado.wicket.participate.ui.administration.role;

import de.agilecoders.wicket.core.markup.html.bootstrap.tabs.AjaxBootstrapTabbedPanel;
import de.vinado.wicket.participate.component.modal.BootstrapModal;
import de.vinado.wicket.participate.component.modal.BootstrapModalPanel;
import de.vinado.wicket.participate.data.dto.PersonRoleDTO;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class AddRemovePersonToRolePanel extends BootstrapModalPanel<PersonRoleDTO> {


    public AddRemovePersonToRolePanel(final BootstrapModal modal, final IModel<PersonRoleDTO> model) {
        super(modal, new ResourceModel("roles", "Roles"), model);

        final List<ITab> tabs = new ArrayList<>();
        tabs.add(new AbstractTab(new ResourceModel("role.add", "Add Role")) {
            @Override
            public Panel getPanel(final String panelId) {
                return new AddPersonToRole(panelId);
            }
        });
        tabs.add(new AbstractTab(new ResourceModel("role.remove", "Remove Role")) {
            @Override
            public Panel getPanel(final String panelId) {
                return new RemovePersonToRole(panelId);
            }
        });

        final AjaxBootstrapTabbedPanel<ITab> tabbedPanel = new AjaxBootstrapTabbedPanel<>("tabbedPanel", tabs);
        inner.add(tabbedPanel);
    }

    @Override
    protected void onSaveSubmit(final IModel<PersonRoleDTO> model, final AjaxRequestTarget target) {

    }

    private class AddPersonToRole extends Panel {

        private AddPersonToRole(final String id) {
            super(id);


        }
    }

    private class RemovePersonToRole extends Panel {

        private RemovePersonToRole(final String id) {
            super(id);


        }
    }
}
