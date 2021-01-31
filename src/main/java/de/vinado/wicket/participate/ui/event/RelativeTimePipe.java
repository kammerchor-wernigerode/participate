package de.vinado.wicket.participate.ui.event;

import de.vinado.wicket.participate.resources.js.RelativeTimePipeJsResourceReference;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.basic.Label;

public class RelativeTimePipe extends Behavior {

    @Override
    public void onComponentTag(final Component component, final ComponentTag tag) {
        tag.put("data-date", component.getDefaultModelObjectAsString());
    }

    @Override
    public void renderHead(final Component component, final IHeaderResponse response) {
        response.render(JavaScriptHeaderItem.forReference(RelativeTimePipeJsResourceReference.INSTANCE));

        if (component instanceof Label) {
            response.render(new OnDomReadyHeaderItem("rtfTransform(document.getElementById('" + component.getMarkupId() + "'))"));
        }
    }
}
