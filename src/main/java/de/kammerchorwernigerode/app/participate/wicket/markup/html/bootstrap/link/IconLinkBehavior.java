package de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.link;

import de.kammerchorwernigerode.app.participate.wicket.markup.html.util.Attributes;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.util.Components;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.util.CssClassNames;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class IconLinkBehavior extends Behavior {

    private final IModel<Boolean> animate;

    public IconLinkBehavior() {
        this(false);
    }

    public IconLinkBehavior(boolean animate) {
        this(Model.of(animate));
    }

    @Override
    public void onComponentTag(Component component, ComponentTag tag) {
        super.onComponentTag(component, tag);

        Components.assertTag(component, tag, "a");

        if (!component.isEnabledInHierarchy()) {
            tag.put("disabled", "disabled");
        }

        CssClassNames.Builder builder = CssClassNames.builder().add(
            "icon-link", (component.isEnabledInHierarchy() ? "" : "disabled"));

        if (!component.isEnabled()) {
            builder.add("disabled");
            Attributes.set("aria-disabled", "true", tag);
        }

        builder.add(animate.getObject() ? "icon-link-hover" : "");

        Attributes.addClass(tag, builder.toString());
    }

    @Override
    public void detach(Component component) {
        animate.detach();
    }
}
