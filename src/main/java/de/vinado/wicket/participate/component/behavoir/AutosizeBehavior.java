package de.vinado.wicket.participate.component.behavoir;

import de.agilecoders.wicket.core.util.Components;
import de.vinado.wicket.participate.resources.js.AutoHeightJsResourceReference;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.form.TextArea;

/**
 * Adds a script to a {@link org.apache.wicket.markup.html.form.TextArea} to resize the component, when the cursor
 * reaches the end of the textarea.
 *
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class AutosizeBehavior extends Behavior {

    /**
     * {@inheritDoc}
     *
     * @param component {@link org.apache.wicket.Component}
     * @param tag       {@link org.apache.wicket.markup.ComponentTag}
     */
    @Override
    public void onComponentTag(final Component component, final ComponentTag tag) {
        Components.assertTag(component, tag, "textarea");
        tag.put("rows", "1");
    }

    /**
     * {@inheritDoc}
     *
     * @param component {@link org.apache.wicket.Component}
     * @param response  {@link org.apache.wicket.markup.head.IHeaderResponse}
     */
    @Override
    public void renderHead(final Component component, final IHeaderResponse response) {
        response.render(JavaScriptHeaderItem.forReference(AutoHeightJsResourceReference.INSTANCE));

        if (component instanceof TextArea) {
            response.render(new OnDomReadyHeaderItem("autosize(document.getElementById('" + component.getMarkupId() + "'))"));
        }
    }
}
