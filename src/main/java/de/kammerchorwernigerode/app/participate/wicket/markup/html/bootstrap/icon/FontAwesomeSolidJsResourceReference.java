package de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.icon;

import de.agilecoders.wicket.webjars.request.resource.WebjarsJavaScriptResourceReference;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;

import java.util.List;

public class FontAwesomeSolidJsResourceReference extends WebjarsJavaScriptResourceReference {

    private FontAwesomeSolidJsResourceReference() {
        super("font-awesome/current/js/solid.js");
    }

    @Override
    public List<HeaderItem> getDependencies() {
        List<HeaderItem> dependencies = super.getDependencies();
        dependencies.add(FontAwesomeJsResourceReference.asHeaderItem());
        return dependencies;
    }

    public static JavaScriptReferenceHeaderItem asHeaderItem() {
        return JavaScriptHeaderItem.forReference(Holder.INSTANCE);
    }


    private static final class Holder {

        private static final FontAwesomeSolidJsResourceReference INSTANCE = new FontAwesomeSolidJsResourceReference();
    }
}
