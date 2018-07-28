package de.vinado.wicket.participate.ui.pages;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapBookmarkablePageLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.references.BootlintHeaderItem;
import de.agilecoders.wicket.core.markup.html.references.RespondJavaScriptReference;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesomeCssReference;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesomeIconType;
import de.vinado.wicket.participate.component.panel.Footer;
import de.vinado.wicket.participate.resources.css.ParticipateCssResourceReference;
import de.vinado.wicket.participate.resources.js.BusyIndicatorJsResourceReference;
import de.vinado.wicket.participate.resources.js.ParticipateJsResourceReference;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.head.MetaDataHeaderItem;
import org.apache.wicket.markup.head.filter.FilteredHeaderItem;
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
        response.render(MetaDataHeaderItem.forMetaTag("viewport", "width=device-width, initial-scale=1.0, maximum-scale=1, user-scalable=no"));
        response.render(MetaDataHeaderItem.forMetaTag("robots", "noindex, nofollow"));
        response.render(new FilteredHeaderItem(JavaScriptHeaderItem.forReference(ParticipateJsResourceReference.INSTANCE), "footer-container"));
        response.render(RespondJavaScriptReference.headerItem());
        if (!getRequest().getRequestParameters().getParameterValue("bootlint").isNull()) {
            response.render(BootlintHeaderItem.INSTANCE);
        }
        response.render(CssHeaderItem.forReference(FontAwesomeCssReference.instance()));
        response.render(CssReferenceHeaderItem.forReference(ParticipateCssResourceReference.INSTANCE));
        response.render(JavaScriptReferenceHeaderItem.forReference(BusyIndicatorJsResourceReference.INSTANCE));
    }
}
