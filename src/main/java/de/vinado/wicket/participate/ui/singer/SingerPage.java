package de.vinado.wicket.participate.ui.singer;

import de.agilecoders.wicket.core.markup.html.bootstrap.navigation.Breadcrumb;
import de.vinado.wicket.participate.data.Singer;
import de.vinado.wicket.participate.ui.page.ParticipatePage;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * Page provides interaction with {@link Singer}s
 *
 * @author Julius Felchow (julius.felchow@gmail.com)
 */
public class SingerPage extends ParticipatePage {

    public SingerPage() {
        this(new PageParameters());
    }

    /**
     * @param parameters {@link PageParameters}
     */
    public SingerPage(final PageParameters parameters) {
        super(parameters);

        final Breadcrumb breadcrumb = new Breadcrumb("breadcrumb");
        breadcrumb.setOutputMarkupPlaceholderTag(true);
        breadcrumb.setVisible(false);
        add(breadcrumb);

        final BreadCrumbPanel breadCrumbPanel = new SingerTabPanel("singerPanel", breadcrumb);
        breadCrumbPanel.setOutputMarkupId(true);
        breadcrumb.setActive(breadCrumbPanel);
        add(breadCrumbPanel);
    }
}
