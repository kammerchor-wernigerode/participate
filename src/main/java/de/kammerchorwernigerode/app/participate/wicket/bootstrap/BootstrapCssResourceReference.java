package de.kammerchorwernigerode.app.participate.wicket.bootstrap;

import css.Scope;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.request.resource.CssResourceReference;

public class BootstrapCssResourceReference extends CssResourceReference {

    private BootstrapCssResourceReference() {
        super(Scope.class, "bootstrap.css");
    }

    public static CssReferenceHeaderItem asHeaderItem() {
        return CssHeaderItem.forReference(Holder.INSTANCE);
    }


    private static class Holder {

        private static final BootstrapCssResourceReference INSTANCE = new BootstrapCssResourceReference();
    }
}
