package de.kammerchorwernigerode.app.participate.wicket.bootstrap;

import de.agilecoders.wicket.webjars.request.resource.WebjarsJavaScriptResourceReference;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;

public class BootstrapJavaScriptResourceReference extends WebjarsJavaScriptResourceReference {

    private BootstrapJavaScriptResourceReference() {
        super("bootstrap/current/js/bootstrap.bundle.js");
    }

    public static JavaScriptReferenceHeaderItem asHeaderItem() {
        return JavaScriptHeaderItem.forReference(Holder.INSTANCE);
    }


    private static class Holder {

        private static final BootstrapJavaScriptResourceReference INSTANCE = new BootstrapJavaScriptResourceReference();
    }
}
