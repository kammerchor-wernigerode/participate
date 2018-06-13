package de.vinado.wicket.participate.resources.js;

import de.vinado.wicket.participate.resources.css.SnackbarCssResourceReference;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.request.resource.CssResourceReference;

import java.util.List;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class SnackbarJsResourceReference extends CssResourceReference {

    public static final SnackbarJsResourceReference INSTANCE = new SnackbarJsResourceReference();


    public SnackbarJsResourceReference() {
        super(SnackbarJsResourceReference.class, "snackbar.min.js");
    }

    @Override
    public List<HeaderItem> getDependencies() {
        final List<HeaderItem> dependencies = super.getDependencies();
        dependencies.add(CssHeaderItem.forReference(SnackbarCssResourceReference.INSTANCE));

        return dependencies;
    }
}
