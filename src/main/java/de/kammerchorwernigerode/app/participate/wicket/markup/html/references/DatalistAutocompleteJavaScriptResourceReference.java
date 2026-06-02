package de.kammerchorwernigerode.app.participate.wicket.markup.html.references;

import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

public class DatalistAutocompleteJavaScriptResourceReference extends JavaScriptResourceReference {

    private DatalistAutocompleteJavaScriptResourceReference() {
        super(DatalistAutocompleteJavaScriptResourceReference.class, "datalist-autocomplete.js");
    }

    public static JavaScriptReferenceHeaderItem asHeaderItem() {
        return JavaScriptHeaderItem.forReference(Holder.INSTANCE);
    }


    private static class Holder {

        private static final DatalistAutocompleteJavaScriptResourceReference INSTANCE =
            new DatalistAutocompleteJavaScriptResourceReference();
    }
}
