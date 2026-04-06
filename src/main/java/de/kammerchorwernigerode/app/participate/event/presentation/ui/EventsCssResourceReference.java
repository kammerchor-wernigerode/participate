package de.kammerchorwernigerode.app.participate.event.presentation.ui;

import css.Scope;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.request.resource.CssResourceReference;

public class EventsCssResourceReference extends CssResourceReference {

    private EventsCssResourceReference() {
        super(Scope.class, "participate-events.css");
    }

    public static CssReferenceHeaderItem asHeaderItem() {
        return CssHeaderItem.forReference(Holder.INSTANCE);
    }


    private static class Holder {

        private static final EventsCssResourceReference INSTANCE = new EventsCssResourceReference();
    }
}
