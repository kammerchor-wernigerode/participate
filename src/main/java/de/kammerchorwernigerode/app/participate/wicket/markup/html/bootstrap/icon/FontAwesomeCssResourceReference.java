package de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.icon;

import de.agilecoders.wicket.webjars.request.resource.WebjarsCssResourceReference;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.CssReferenceHeaderItem;

public class FontAwesomeCssResourceReference extends WebjarsCssResourceReference {

    private FontAwesomeCssResourceReference() {
        super("font-awesome/current/css/svg-with-js.css");
    }

    public static CssReferenceHeaderItem asHeaderItem() {
        return CssHeaderItem.forReference(Holder.INSTANCE);
    }


    private static final class Holder {

        private static final FontAwesomeCssResourceReference INSTANCE = new FontAwesomeCssResourceReference();
    }
}
