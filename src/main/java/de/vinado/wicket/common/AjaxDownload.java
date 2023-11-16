package de.vinado.wicket.common;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AbstractAjaxBehavior;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.resource.ContentDisposition;
import org.apache.wicket.util.resource.IResourceStream;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class AjaxDownload extends AbstractAjaxBehavior {

    private IResourceStream resourceStream;

    private String fileName;

    private boolean antiCache;

    public AjaxDownload() {
        this(true);
    }

    public AjaxDownload(boolean antiCache) {
        this.antiCache = antiCache;
    }

    public void go(AjaxRequestTarget target, IResourceStream resourceStream, String fileName) {
        this.resourceStream = resourceStream;
        this.fileName = fileName;

        String callbackUrl = getCallbackUrl().toString();

        if (antiCache) {
            callbackUrl = callbackUrl + (callbackUrl.contains("?") ? "&" : "?");
            callbackUrl = callbackUrl + "antiCache=" + System.currentTimeMillis();
        }

        target.appendJavaScript("setTimeout(\"window.location.href='" + callbackUrl + "'\", 100);");
    }

    @Override
    public void onRequest() {
        ResourceStreamRequestHandler handler = new ResourceStreamRequestHandler(resourceStream, fileName);
        handler.setContentDisposition(ContentDisposition.ATTACHMENT);
        getComponent().getRequestCycle().scheduleRequestHandlerAfterCurrent(handler);
    }
}
