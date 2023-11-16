package de.vinado.wicket.bt4.tooltip;

import de.agilecoders.wicket.core.markup.html.bootstrap.components.TooltipConfig;
import de.agilecoders.wicket.jquery.function.IFunction;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.model.IModel;

import static de.agilecoders.wicket.jquery.JQuery.$;

public class TooltipBehavior extends de.agilecoders.wicket.core.markup.html.bootstrap.components.TooltipBehavior {

    public TooltipBehavior(IModel<String> label) {
        super(label);
    }

    public TooltipBehavior(IModel<String> label, TooltipConfig config) {
        super(label, config);
    }

    @Override
    public void bind(Component component) {
        super.bind(component);

        if (component instanceof AbstractLink || component instanceof Button) {
            component.add(new AjaxEventBehavior("click") {
                @Override
                protected void onEvent(AjaxRequestTarget target) {
                    target.appendJavaScript($(component).chain((IFunction) () -> "tooltip('dispose')").get());
                }
            });
        }
    }
}
