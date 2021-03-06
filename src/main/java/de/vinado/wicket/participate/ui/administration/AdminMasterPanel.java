package de.vinado.wicket.participate.ui.administration;

import de.agilecoders.wicket.core.markup.html.bootstrap.tabs.AjaxBootstrapTabbedPanel;
import de.vinado.wicket.participate.ui.administration.tool.ToolPanel;
import de.vinado.wicket.participate.ui.administration.user.UserPanel;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanel;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Administration main panel
 *
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class AdminMasterPanel extends BreadCrumbPanel {

    public AdminMasterPanel(final String id, final IBreadCrumbModel breadCrumbModel) {
        super(id, breadCrumbModel);

        final List<ITab> tabs = new ArrayList<>();
        tabs.add(new AbstractTab(new ResourceModel("tools", "Tools")) {
            @Override
            public WebMarkupContainer getPanel(final String panelId) {
                return new ToolPanel(panelId);
            }
        });
        tabs.add(new AbstractTab(new ResourceModel("tools.user-management", "User Management")) {
            @Override
            public WebMarkupContainer getPanel(final String panelId) {
                return new UserPanel(panelId);
            }
        });

        final AjaxBootstrapTabbedPanel tabbedPanel = new AjaxBootstrapTabbedPanel<>("tabbedPanel", tabs);
        add(tabbedPanel);
    }

    @Override
    public IModel<String> getTitle() {
        return new ResourceModel("administration", "Administration");
    }
}
