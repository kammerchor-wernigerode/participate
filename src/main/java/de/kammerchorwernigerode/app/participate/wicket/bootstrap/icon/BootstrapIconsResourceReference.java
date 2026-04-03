package de.kammerchorwernigerode.app.participate.wicket.bootstrap.icon;

import de.agilecoders.wicket.webjars.request.resource.WebjarsCssResourceReference;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.CssReferenceHeaderItem;

public class BootstrapIconsResourceReference extends WebjarsCssResourceReference {

    private BootstrapIconsResourceReference() {
        super("bootstrap-icons/current/font/bootstrap-icons.css");
    }

    public static CssReferenceHeaderItem asHeaderItem() {
        return CssHeaderItem.forReference(Holder.INSTANCE);
    }


    private static class Holder {

        private static final BootstrapIconsResourceReference INSTANCE = new BootstrapIconsResourceReference();
    }
}
