package de.vinado.wicket.participate.resources.js;

import com.google.common.collect.Lists;
import org.apache.wicket.Application;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

import java.util.List;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class MatchHeightJsResourceReference extends JavaScriptResourceReference {

    public static final MatchHeightJsResourceReference INSTANCE = new MatchHeightJsResourceReference();

    public MatchHeightJsResourceReference() {
        super(MatchHeightJsResourceReference.class, "jquery.matchHeight-min.js");
    }

    @Override
    public List<HeaderItem> getDependencies() {
        final List<HeaderItem> dependencies = Lists.newArrayList(super.getDependencies());
        dependencies.add(JavaScriptHeaderItem.forReference(Application.get().getJavaScriptLibrarySettings().getJQueryReference()));

        return dependencies;
    }
}
