package de.vinado.wicket.participate.ui.administration;

import de.agilecoders.wicket.core.markup.html.bootstrap.navigation.Breadcrumb;
import de.vinado.wicket.participate.ui.pages.ParticipatePage;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * Administration page
 *
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
//@AuthorizeInstantiation(Roles.ADMIN)
public class AdminPage extends ParticipatePage {

    public AdminPage() {
        this(new PageParameters());
    }

    /**
     * @param parameters Page parameters
     */
    public AdminPage(final PageParameters parameters) {
        super(parameters);

        final Breadcrumb breadcrumb = new Breadcrumb("breadcrumb");
        breadcrumb.setOutputMarkupPlaceholderTag(true);
        breadcrumb.setVisible(false);
        add(breadcrumb);

        final BreadCrumbPanel breadCrumbPanel = new AdminMasterPanel("adminPanel", breadcrumb);
        breadCrumbPanel.setOutputMarkupId(true);
        breadcrumb.setActive(breadCrumbPanel);
        add(breadCrumbPanel);
    }
}
