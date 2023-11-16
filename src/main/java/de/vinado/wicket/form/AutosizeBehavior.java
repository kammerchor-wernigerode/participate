package de.vinado.wicket.form;

import de.agilecoders.wicket.core.util.Components;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.form.FormComponent;

public class AutosizeBehavior extends Behavior {

    @Override
    public void bind(Component component) {
        super.bind(component);
        component.setOutputMarkupId(true);
    }

    @Override
    public void onComponentTag(Component component, ComponentTag tag) {
        Components.assertTag(component, tag, "textarea");

        String value = ((FormComponent<?>) component).getValue();
        tag.put("rows", StringUtils.countMatches(value, '\n'));
    }

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        super.renderHead(component, response);
        response.render(AutosizeJsReference.asHeaderItem());
        response.render(OnDomReadyHeaderItem.forScript("autosize(document.getElementById('" + component.getMarkupId() + "'))"));
    }

    @Override
    public void onEvent(Component component, IEvent<?> event) {
        super.onEvent(component, event);
        if (event.getPayload() instanceof IPartialPageRequestHandler) {
            IPartialPageRequestHandler target = (IPartialPageRequestHandler) event.getPayload();
            if (target.getComponents().contains(component)) {
                target.prependJavaScript("autosize.destroy(document.getElementById('" + component.getMarkupId() + "'));");
            }
        }
    }
}
