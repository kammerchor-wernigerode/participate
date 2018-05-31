package de.vinado.wicket.participate.ui.page;

import de.agilecoders.wicket.core.markup.html.bootstrap.html.HtmlTag;
import de.agilecoders.wicket.core.markup.html.bootstrap.html.IeEdgeMetaTag;
import de.agilecoders.wicket.core.markup.html.bootstrap.html.MetaTag;
import de.agilecoders.wicket.core.markup.html.references.BootlintHeaderItem;
import de.agilecoders.wicket.core.markup.html.references.RespondJavaScriptReference;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesomeCssReference;
import de.vinado.wicket.participate.ParticipateApplication;
import de.vinado.wicket.participate.component.modal.BootstrapModal;
import de.vinado.wicket.participate.resources.css.ParticipateCssResourceReference;
import de.vinado.wicket.participate.resources.css.SnackbarCssResourceReference;
import de.vinado.wicket.participate.resources.js.BusyIndicatorJsResourceReference;
import de.vinado.wicket.participate.resources.js.ParticipateJsResourceReference;
import de.vinado.wicket.participate.resources.js.SnackbarJsResourceReference;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.head.MetaDataHeaderItem;
import org.apache.wicket.markup.head.filter.FilteredHeaderItem;
import org.apache.wicket.markup.head.filter.HeaderResponseContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import java.util.Locale;

/**
 * Base page for the Application. All pages will inherit this Page.
 *
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public abstract class BasePage extends WebPage {

    private BootstrapModal modal;

    /**
     * Construct.
     */
    public BasePage() {
        this(new PageParameters());
    }

    /**
     * Construct with page parameters
     *
     * @param parameters {@link PageParameters}
     */
    public BasePage(final PageParameters parameters) {
        super(parameters);

        addModal(ParticipatePage.MODAL_ID);

        add(new HtmlTag("html", Locale.GERMAN));
        add(new IeEdgeMetaTag("xUaCompatible"));
        add(new MetaTag("author", Model.of("author"), Model.of("Vincent Nadoll, Julius Felchow")));
        add(new Label("title", ParticipateApplication.get().getApplicationName()));

        add(new HeaderResponseContainer("footer-container", "footer-container"));
    }

    /**
     * Usage: final BootstrapModal modal = ((BasePage) getWebPage()).getModal;
     *
     * @return {@link de.vinado.wicket.participate.component.modal.BootstrapModal}
     */
    @SuppressWarnings("unused")
    public BootstrapModal getModal() {
        return modal;
    }

    private void addModal(final String id) {
        modal = new BootstrapModal(id);
        add(modal);
    }

    /**
     * I have no clue, what this does.
     * {@inheritDoc}
     *
     * @return false
     */
    @Override
    public boolean isVersioned() {
        return false;
    }

    /**
     * Includes all needed resources and sets the meta tags.
     * {@inheritDoc}
     *
     * @param response {@link IHeaderResponse}
     */
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
        response.render(CssReferenceHeaderItem.forReference(SnackbarCssResourceReference.INSTANCE));
        response.render(JavaScriptReferenceHeaderItem.forReference(SnackbarJsResourceReference.INSTANCE));
        response.render(JavaScriptReferenceHeaderItem.forReference(BusyIndicatorJsResourceReference.INSTANCE));
    }
}
