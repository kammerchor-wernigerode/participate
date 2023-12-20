package de.vinado.wicket.bt4.tooltip;

import de.agilecoders.wicket.core.markup.html.bootstrap.components.TooltipConfig;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.model.IModel;

public class TooltipBehavior extends Behavior {

    public TooltipBehavior(IModel<String> label) {
    }

    public TooltipBehavior(IModel<String> label, TooltipConfig config) {
    }
}
