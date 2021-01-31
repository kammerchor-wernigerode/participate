package de.vinado.wicket.participate.resources.js;

import org.apache.wicket.Application;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

import java.util.List;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class RelativeTimePipeJsResourceReference extends JavaScriptResourceReference {

    public static final RelativeTimePipeJsResourceReference INSTANCE = new RelativeTimePipeJsResourceReference();

    public RelativeTimePipeJsResourceReference() {
        super(RelativeTimePipeJsResourceReference.class, "relative-time.pipe.js");
    }

    @Override
    public List<HeaderItem> getDependencies() {
        final List<HeaderItem> dependencies = super.getDependencies();
        dependencies.add(JavaScriptHeaderItem.forReference(Application.get().getJavaScriptLibrarySettings().getJQueryReference()));

        return dependencies;
    }
}
