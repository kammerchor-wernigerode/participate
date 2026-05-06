package de.kammerchorwernigerode.app.participate.wicket;

import css.Scope;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.request.resource.CssResourceReference;

public class ParticipateCssResourceReference extends CssResourceReference {

    private ParticipateCssResourceReference() {
        super(Scope.class, "participate.css");
    }

    public static CssReferenceHeaderItem asHeaderItem() {
        return CssHeaderItem.forReference(Holder.INSTANCE);
    }


    private static class Holder {

        private static final ParticipateCssResourceReference INSTANCE = new ParticipateCssResourceReference();
    }
}
