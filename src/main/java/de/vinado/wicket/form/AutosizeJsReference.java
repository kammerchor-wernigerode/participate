package de.vinado.wicket.form;

import de.agilecoders.wicket.webjars.request.resource.WebjarsJavaScriptResourceReference;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;

public class AutosizeJsReference extends WebjarsJavaScriptResourceReference {

    private AutosizeJsReference() {
        super("autosize/current/dist/autosize.min.js");
    }

    public static HeaderItem asHeaderItem() {
        return JavaScriptHeaderItem.forReference(getInstance());
    }

    public static AutosizeJsReference getInstance() {
        return Holder.INSTANCE;
    }

    private static final class Holder {
        private static final AutosizeJsReference INSTANCE = new AutosizeJsReference();
    }
}
