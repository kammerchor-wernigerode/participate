package de.vinado.wicket.participate.ui.administration;

import de.agilecoders.wicket.core.markup.html.bootstrap.tabs.AjaxBootstrapTabbedPanel;
import de.vinado.wicket.participate.ui.administration.tool.ToolPanel;
import de.vinado.wicket.participate.ui.administration.user.UserPanel;
import de.vinado.wicket.participate.ui.pages.ParticipatePage;
import de.vinado.wicket.tabs.LambdaTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vincent Nadoll
 */
public class AdminPage extends ParticipatePage {

    private static final long serialVersionUID = 1365961032818332554L;

    public AdminPage(PageParameters parameters) {
        super(parameters);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(tabs("tabbedPanel"));
    }

    private WebMarkupContainer tabs(String id) {
        List<ITab> tabs = content();
        return new AjaxBootstrapTabbedPanel<>(id, tabs);
    }

    private List<ITab> content() {
        List<ITab> tabs = new ArrayList<>();
        tabs.add(new LambdaTab(new ResourceModel("tools", "Tools"), ToolPanel::new));
        tabs.add(new LambdaTab(new ResourceModel("tools.user-management", "User Management"), UserPanel::new));
        return tabs;
    }
}
