package de.vinado.wicket.participate.resources.css;

import de.agilecoders.wicket.webjars.request.resource.WebjarsCssResourceReference;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.HeaderItem;

/**
 * @author Vincent Nadoll
 */
public class SnackbarCssResourceReference extends WebjarsCssResourceReference {

    private static final long serialVersionUID = 4407525623180747946L;

    private SnackbarCssResourceReference() {
        super("node-snackbar/current/snackbar.css");
    }

    public static HeaderItem asHeaderItem() {
        return CssHeaderItem.forReference(instance());
    }

    public static SnackbarCssResourceReference instance() {
        return Holder.INSTANCE;
    }


    private static final class Holder {
        private static final SnackbarCssResourceReference INSTANCE = new SnackbarCssResourceReference();
    }
}
