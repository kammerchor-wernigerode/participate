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

    public AjaxDownload(final boolean antiCache) {
        this.antiCache = antiCache;
    }

    public void go(final AjaxRequestTarget target, final IResourceStream resourceStream, final String fileName) {
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
        final ResourceStreamRequestHandler handler = new ResourceStreamRequestHandler(resourceStream, fileName);
        handler.setContentDisposition(ContentDisposition.ATTACHMENT);
        getComponent().getRequestCycle().scheduleRequestHandlerAfterCurrent(handler);
    }
}
