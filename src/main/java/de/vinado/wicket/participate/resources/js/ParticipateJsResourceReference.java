package de.vinado.wicket.participate.resources.js;

import com.google.common.collect.Lists;
import de.agilecoders.wicket.core.Bootstrap;
import de.vinado.wicket.participate.resources.css.ParticipateCssResourceReference;
import org.apache.wicket.Application;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.request.resource.JavaScriptResourceReference;

import java.util.List;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class ParticipateJsResourceReference extends JavaScriptResourceReference {

    public static final ParticipateJsResourceReference INSTANCE = new ParticipateJsResourceReference();

    private ParticipateJsResourceReference() {
        super(ParticipateJsResourceReference.class, "participate.js");
    }

    @Override
    public List<HeaderItem> getDependencies() {
        final List<HeaderItem> dependencies = Lists.newArrayList(super.getDependencies());
        dependencies.add(CssHeaderItem.forReference(ParticipateCssResourceReference.INSTANCE));
        dependencies.add(JavaScriptHeaderItem.forReference(Application.get().getJavaScriptLibrarySettings().getJQueryReference()));
        dependencies.add(JavaScriptHeaderItem.forReference(Bootstrap.getSettings().getJsResourceReference()));

        return dependencies;
    }
}
