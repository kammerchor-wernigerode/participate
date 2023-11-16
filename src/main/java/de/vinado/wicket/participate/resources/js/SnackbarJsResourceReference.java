package de.vinado.wicket.participate.resources.js;

import de.agilecoders.wicket.core.util.Dependencies;
import de.agilecoders.wicket.webjars.request.resource.WebjarsJavaScriptResourceReference;
import de.vinado.wicket.participate.resources.css.SnackbarCssResourceReference;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;

import java.util.List;

public class SnackbarJsResourceReference extends WebjarsJavaScriptResourceReference {

    private SnackbarJsResourceReference() {
        super("node-snackbar/current/snackbar.js");
    }

    public static HeaderItem asHeaderItem() {
        return JavaScriptHeaderItem.forReference(instance());
    }

    public static SnackbarJsResourceReference instance() {
        return Holder.INSTANCE;
    }

    @Override
    public List<HeaderItem> getDependencies() {
        return Dependencies.combine(
            super.getDependencies(),
            SnackbarCssResourceReference.asHeaderItem()
        );
    }


    private static final class Holder {
        private static final SnackbarJsResourceReference INSTANCE = new SnackbarJsResourceReference();
    }
}
