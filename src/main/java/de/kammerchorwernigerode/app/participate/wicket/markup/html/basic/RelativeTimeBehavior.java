package de.kammerchorwernigerode.app.participate.wicket.markup.html.basic;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.basic.Label;

public class RelativeTimeBehavior extends Behavior {

    @Override
    public void onComponentTag(Component component, ComponentTag tag) {
        tag.put("data-date", component.getDefaultModelObjectAsString());
    }

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        response.render(JavaScriptHeaderItem.forUrl("js/relative-time.pipe.js"));

        if (component instanceof Label) {
            String javaScript = "rtfTransform(document.getElementById('" + component.getMarkupId() + "'))";
            response.render(new OnDomReadyHeaderItem(javaScript));
        }
    }
}
