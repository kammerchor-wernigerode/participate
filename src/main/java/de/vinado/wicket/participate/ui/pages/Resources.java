package de.vinado.wicket.participate.ui.pages;

import de.agilecoders.wicket.core.markup.html.references.BootlintHeaderItem;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome5CssReference;
import de.vinado.wicket.participate.resources.FaviconResourceReference;
import de.vinado.wicket.participate.resources.css.ParticipateCssResourceReference;
import de.vinado.wicket.participate.resources.js.BusyIndicatorJsResourceReference;
import de.vinado.wicket.participate.resources.js.ParticipateJsResourceReference;
import lombok.experimental.UtilityClass;
import org.apache.wicket.Component;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.head.MetaDataHeaderItem;

@UtilityClass
public final class Resources {

    public static void render(IHeaderResponse response, Component host) {
        response.render(MetaDataHeaderItem.forMetaTag("viewport", "width=device-width, initial-scale=1.0, maximum-scale=1, user-scalable=no"));
        response.render(MetaDataHeaderItem.forMetaTag("robots", "noindex, nofollow"));
        response.render(MetaDataHeaderItem.forLinkTag("shortcut icon", host.urlFor(FaviconResourceReference.INSTANCE, null).toString()));
        response.render(JavaScriptHeaderItem.forReference(ParticipateJsResourceReference.INSTANCE));
        if (!host.getRequest().getRequestParameters().getParameterValue("bootlint").isNull()) {
            response.render(BootlintHeaderItem.INSTANCE);
        }
        response.render(CssHeaderItem.forReference(FontAwesome5CssReference.instance()));
        response.render(CssReferenceHeaderItem.forReference(ParticipateCssResourceReference.INSTANCE));
        response.render(JavaScriptReferenceHeaderItem.forReference(BusyIndicatorJsResourceReference.INSTANCE));
    }
}
