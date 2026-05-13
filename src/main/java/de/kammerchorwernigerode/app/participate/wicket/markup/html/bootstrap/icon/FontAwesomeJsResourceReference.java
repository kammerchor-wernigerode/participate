package de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.icon;

import de.agilecoders.wicket.webjars.request.resource.WebjarsJavaScriptResourceReference;
import org.apache.wicket.core.util.string.JavaScriptUtils;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.markup.head.JavaScriptReferenceType;
import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.handler.resource.ResourceReferenceRequestHandler;
import org.apache.wicket.util.value.AttributeMap;

import java.util.List;

class FontAwesomeJsResourceReference extends WebjarsJavaScriptResourceReference {

    public static final JavaScriptReferenceType TYPE = JavaScriptReferenceType.TEXT_JAVASCRIPT;

    private FontAwesomeJsResourceReference() {
        super("font-awesome/current/js/fontawesome.js");
    }

    @Override
    public List<HeaderItem> getDependencies() {
        List<HeaderItem> dependencies = super.getDependencies();
        dependencies.add(FontAwesomeCssResourceReference.asHeaderItem());
        return dependencies;
    }

    public static JavaScriptReferenceHeaderItem asHeaderItem() {
        return new JavaScriptReferenceHeaderItem(Holder.INSTANCE, null, null) {

            @Override
            public void render(Response response) {
                AttributeMap attributes = new AttributeMap();
                attributes.putAttribute(JavaScriptUtils.ATTR_TYPE, TYPE.getType());
                attributes.putAttribute(JavaScriptUtils.ATTR_SCRIPT_SRC, getUrl());
                attributes.putAttribute(JavaScriptUtils.ATTR_CSP_NONCE, getNonce());
                attributes.putAttribute("data-auto-add-css", "false");
                JavaScriptUtils.writeScript(response, attributes);
            }

            private String getUrl() {
                IRequestHandler handler = new ResourceReferenceRequestHandler(getReference(), getPageParameters());
                return RequestCycle.get().urlFor(handler).toString();
            }
        };
    }


    private static final class Holder {

        private static final FontAwesomeJsResourceReference INSTANCE = new FontAwesomeJsResourceReference();
    }
}
