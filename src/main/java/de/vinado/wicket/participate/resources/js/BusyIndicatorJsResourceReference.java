package de.vinado.wicket.participate.resources.js;

import de.vinado.wicket.participate.resources.css.ParticipateCssResourceReference;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

import java.util.List;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class BusyIndicatorJsResourceReference extends JavaScriptResourceReference {

    public static final BusyIndicatorJsResourceReference INSTANCE = new BusyIndicatorJsResourceReference();

    public BusyIndicatorJsResourceReference() {
        super(BusyIndicatorJsResourceReference.class, "busy-indicator.js");
    }

    @Override
    public List<HeaderItem> getDependencies() {
        final List<HeaderItem> dependencies = super.getDependencies();
        dependencies.add(CssHeaderItem.forReference(ParticipateCssResourceReference.INSTANCE));

        return dependencies;
    }
}
