package de.vinado.wicket.participate.ui.pages;

import de.agilecoders.wicket.core.markup.html.references.BootlintHeaderItem;
import de.vinado.app.participate.wicket.icon.FontAwesome5JsReference;
import de.vinado.wicket.participate.resources.css.ParticipateCssResourceReference;
import de.vinado.wicket.participate.resources.js.BusyIndicatorJsResourceReference;
import de.vinado.wicket.participate.resources.js.ParticipateJsResourceReference;
import lombok.experimental.UtilityClass;
import org.apache.wicket.Component;
import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.head.MetaDataHeaderItem;
import org.apache.wicket.markup.head.filter.FilteredHeaderItem;

import static org.apache.wicket.markup.head.HtmlImportHeaderItem.forLinkTag;

@UtilityClass
public final class Resources {

    public static void render(IHeaderResponse response, Component host) {
        response.render(MetaDataHeaderItem.forMetaTag("viewport", "width=device-width, initial-scale=1.0, maximum-scale=1, user-scalable=no"));
        response.render(MetaDataHeaderItem.forMetaTag("robots", "noindex, nofollow"));
        renderFavicons(response);
        response.render(new FilteredHeaderItem(JavaScriptHeaderItem.forReference(ParticipateJsResourceReference.INSTANCE), "footer-container"));
        if (!host.getRequest().getRequestParameters().getParameterValue("bootlint").isNull()) {
            response.render(BootlintHeaderItem.INSTANCE);
        }
        response.render(JavaScriptReferenceHeaderItem.forReference(FontAwesome5JsReference.getInstance()));
        response.render(CssReferenceHeaderItem.forReference(ParticipateCssResourceReference.INSTANCE));
        response.render(JavaScriptReferenceHeaderItem.forReference(BusyIndicatorJsResourceReference.INSTANCE));
    }

    public static void renderFavicons(IHeaderResponse response) {
        response.render(forLinkTag("icon", "/favicon.svg").addTagAttribute("type", "image/svg+xml"));
        response.render(forLinkTag("icon", "/favicon.png").addTagAttribute("type", "image/png"));
    }
}
