package de.vinado.wicket.participate.ui.pages;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapBookmarkablePageLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesomeIconType;
import de.vinado.wicket.participate.components.panels.Footer;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.filter.HeaderResponseContainer;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.http.WebResponse;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public abstract class AbstractErrorPage extends org.apache.wicket.markup.html.pages.AbstractErrorPage {

    public AbstractErrorPage() {
        this(new PageParameters());
    }

    public AbstractErrorPage(final PageParameters parameters) {
        super(parameters);

        final BootstrapBookmarkablePageLink<String> homePageLink = new BootstrapBookmarkablePageLink<>("homePageLink",
            getApplication().getHomePage(), Buttons.Type.Primary);
        homePageLink.setIconType(FontAwesomeIconType.home);
        homePageLink.setLabel(new ResourceModel("navigate.homepage", "Goto Homepage"));
        addHomePageLink(homePageLink);

        final Footer footer = new Footer("footer");
        footer.setVisible(showFooter());
        add(footer);

        add(new HeaderResponseContainer("footer-container", "footer-container"));
    }

    protected abstract void addHomePageLink(final AbstractLink homePageLink);

    protected abstract int getStatusCode();

    protected boolean showFooter() {
        return true;
    }

    @Override
    protected void setHeaders(final WebResponse response) {
        super.setHeaders(response);
        response.setStatus(getStatusCode());
    }

    @Override
    public boolean isVersioned() {
        return false;
    }

    @Override
    public void renderHead(final IHeaderResponse response) {
        Resources.render(response, this);
    }
}
