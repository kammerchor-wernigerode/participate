package de.vinado.wicket.participate.resources.css;

import org.apache.wicket.request.resource.CssResourceReference;

public class ParticipateCssResourceReference extends CssResourceReference {

    public static final ParticipateCssResourceReference INSTANCE = new ParticipateCssResourceReference();

    public ParticipateCssResourceReference() {
        super(ParticipateCssResourceReference.class, "participate.css");
    }
}
