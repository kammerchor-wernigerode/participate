package de.vinado.wicket.participate.ui.administration;

import de.agilecoders.wicket.core.markup.html.bootstrap.tabs.AjaxBootstrapTabbedPanel;
import de.vinado.wicket.participate.ui.administration.person.PersonAdministrationPanel;
import de.vinado.wicket.participate.ui.administration.tool.ToolPanel;
import de.vinado.wicket.participate.ui.administration.user.UserPanel;
import de.vinado.wicket.participate.ui.pages.ParticipatePage;
import de.vinado.wicket.tabs.LambdaTab;
import org.apache.wicket.IGenericComponent;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Vincent Nadoll
 */
public class AdminPage extends ParticipatePage implements IGenericComponent<Integer, AdminPage> {

    private static final long serialVersionUID = 1365961032818332554L;

    public AdminPage(PageParameters parameters) {
        super(parameters);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        handleTabParameter();

        add(tabs("tabbedPanel"));
    }

    private void handleTabParameter() {
        tabIndex(getPageParameters())
            .map(Model::of)
            .ifPresent(this::setModel);
    }

    private Optional<Integer> tabIndex(PageParameters parameters) {
        return Optional.ofNullable(parameters.get("tab"))
            .map(StringValue::toOptionalInteger);
    }

    private WebMarkupContainer tabs(String id) {
        List<ITab> tabs = content();
        return new AjaxBootstrapTabbedPanel<>(id, tabs, getModel());
    }

    private List<ITab> content() {
        List<ITab> tabs = new ArrayList<>();
        tabs.add(new LambdaTab(new ResourceModel("tools", "Tools"), ToolPanel::new));
        tabs.add(new LambdaTab(new ResourceModel("tools.user-management", "User Management"), UserPanel::new));
        tabs.add(new LambdaTab(new ResourceModel("persons", "Persons"), PersonAdministrationPanel::new));
        return tabs;
    }
}
