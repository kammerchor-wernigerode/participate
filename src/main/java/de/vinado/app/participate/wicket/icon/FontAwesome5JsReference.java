package de.vinado.app.participate.wicket.icon;

import de.agilecoders.wicket.webjars.request.resource.WebjarsJavaScriptResourceReference;

public class FontAwesome5JsReference extends WebjarsJavaScriptResourceReference {

    private FontAwesome5JsReference() {
        super("font-awesome/current/js/all.js");
    }

    public static FontAwesome5JsReference getInstance() {
        return Holder.INSTANCE;
    }


    private static final class Holder {

        private static final FontAwesome5JsReference INSTANCE = new FontAwesome5JsReference();
    }
}
