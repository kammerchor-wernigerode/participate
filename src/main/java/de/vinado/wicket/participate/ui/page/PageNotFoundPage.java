package de.vinado.wicket.participate.ui.page;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapBookmarkablePageLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesomeIconType;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.http.WebResponse;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class PageNotFoundPage extends AbstractErrorPage {


    public PageNotFoundPage() {
        final BootstrapBookmarkablePageLink<String> homePageLink = new BootstrapBookmarkablePageLink<>("homePageLink",
                getApplication().getHomePage(), Buttons.Type.Primary);
        homePageLink.setIconType(FontAwesomeIconType.home);
        homePageLink.setSize(Buttons.Size.Large);
        homePageLink.setLabel(new ResourceModel("navigate.homepage", "Goto Homepage"));
        add(homePageLink);
    }

    @Override
    protected void setHeaders(final WebResponse response) {
        super.setHeaders(response);
        response.setStatus(404);
    }
}
