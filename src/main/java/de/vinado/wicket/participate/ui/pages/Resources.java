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

import java.util.Arrays;
import java.util.stream.Stream;

import static org.apache.wicket.markup.head.HtmlImportHeaderItem.forLinkTag;
import static org.apache.wicket.markup.head.MetaDataHeaderItem.forMetaTag;

@UtilityClass
public final class Resources {

    private static final int[] appleTouchIconSizes;
    private static final int[] iconSizes;

    static {
        appleTouchIconSizes = new int[]{57, 60, 72, 76, 114, 120, 144, 152, 180};
        iconSizes = new int[]{96, 32, 16};
    }

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
        appleTouchIcons().forEach(response::render);
        response.render(androidIcon());
        pngIcons().forEach(response::render);
        response.render(forLinkTag("manifest", "/manifest.json"));
        response.render(forMetaTag("msapplication-TileColor", "#ffffff"));
        response.render(forMetaTag("msapplication-TileImage", "/ms-icon-144x144.png"));
        response.render(forMetaTag("theme-color", "#ffffff"));
    }

    private static Stream<MetaDataHeaderItem> appleTouchIcons() {
        return Arrays.stream(appleTouchIconSizes).mapToObj(Resources::appleTouchIcon);
    }

    private static MetaDataHeaderItem appleTouchIcon(int size) {
        String dimension = size + "x" + size;
        String path = "/apple-icon-" + dimension + ".png";
        return forLinkTag("apple-touch-icon", path)
            .addTagAttribute("sizes", dimension);
    }

    private static MetaDataHeaderItem androidIcon() {
        return forLinkTag("icon", "/android-icon-192x192.png")
            .addTagAttribute("type", "image/png")
            .addTagAttribute("sizes", "192x192");
    }

    private static Stream<MetaDataHeaderItem> pngIcons() {
        return Arrays.stream(iconSizes).mapToObj(Resources::pngIcon);
    }

    private static MetaDataHeaderItem pngIcon(int size) {
        String dimension = size + "x" + size;
        String href = "/favicon-" + dimension + ".png";
        return forLinkTag("icon", href)
            .addTagAttribute("type", "image/png")
            .addTagAttribute("sizes", dimension);
    }
}
