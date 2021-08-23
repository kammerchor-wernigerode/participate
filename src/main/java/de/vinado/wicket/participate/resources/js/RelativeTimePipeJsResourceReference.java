package de.vinado.wicket.participate.resources.js;

import org.apache.wicket.request.resource.JavaScriptResourceReference;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class RelativeTimePipeJsResourceReference extends JavaScriptResourceReference {

    public static final RelativeTimePipeJsResourceReference INSTANCE = new RelativeTimePipeJsResourceReference();

    public RelativeTimePipeJsResourceReference() {
        super(RelativeTimePipeJsResourceReference.class, "relative-time.pipe.js");
    }
}
