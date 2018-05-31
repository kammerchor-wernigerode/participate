package de.vinado.wicket.participate.resources.css;

import org.apache.wicket.request.resource.CssResourceReference;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class SnackbarCssResourceReference extends CssResourceReference {

    public static final SnackbarCssResourceReference INSTANCE = new SnackbarCssResourceReference();

    public SnackbarCssResourceReference() {
        super(SnackbarCssResourceReference.class, "snackbar.min.css");
    }
}
