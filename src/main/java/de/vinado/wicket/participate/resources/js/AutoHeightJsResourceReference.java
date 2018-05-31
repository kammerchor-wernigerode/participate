package de.vinado.wicket.participate.resources.js;

import org.apache.wicket.request.resource.JavaScriptResourceReference;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class AutoHeightJsResourceReference extends JavaScriptResourceReference {

    public static final AutoHeightJsResourceReference INSTANCE = new AutoHeightJsResourceReference();

    public AutoHeightJsResourceReference() {
        super(AutoHeightJsResourceReference.class, "autosize.min.js");
    }
}
