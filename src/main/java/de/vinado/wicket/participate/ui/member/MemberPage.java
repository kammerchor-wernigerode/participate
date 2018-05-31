package de.vinado.wicket.participate.ui.member;

import de.agilecoders.wicket.core.markup.html.bootstrap.navigation.Breadcrumb;
import de.vinado.wicket.participate.ui.page.ParticipatePage;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * Page provides interaction with {@link de.vinado.wicket.participate.data.Member Members}
 *
 * @author Julius Felchow (julius.felchow@gmail.com)
 */
public class MemberPage extends ParticipatePage {

    public MemberPage() {
        this(new PageParameters());
    }

    /**
     * @param parameters {@link PageParameters}
     */
    public MemberPage(final PageParameters parameters) {
        super(parameters);

        final Breadcrumb breadcrumb = new Breadcrumb("breadcrumb");
        breadcrumb.setOutputMarkupPlaceholderTag(true);
        breadcrumb.setVisible(false);
        add(breadcrumb);

        final BreadCrumbPanel breadCrumbPanel = new MemberTabPanel("memberPanel", breadcrumb);
        breadCrumbPanel.setOutputMarkupId(true);
        breadcrumb.setActive(breadCrumbPanel);
        add(breadCrumbPanel);
    }
}
