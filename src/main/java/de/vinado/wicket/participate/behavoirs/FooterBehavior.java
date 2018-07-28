package de.vinado.wicket.participate.behavoirs;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.BootstrapBaseBehavior;
import de.agilecoders.wicket.core.util.Components;
import org.apache.wicket.Component;
import org.apache.wicket.markup.ComponentTag;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class FooterBehavior extends BootstrapBaseBehavior {

    @Override
    public void onComponentTag(final Component component, final ComponentTag tag) {
        super.onComponentTag(component, tag);

        Components.assertTag(component, tag, "footer");
    }
}
