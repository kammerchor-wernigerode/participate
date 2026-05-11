package de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.button;

import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.button.Buttons.Size;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.button.Buttons.Variant;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.util.Attributes;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.util.Components;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.util.CssClassNames;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.Set;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ButtonBehavior extends Behavior {

    private final IModel<Variant> variant;
    private final IModel<Size> size;

    public ButtonBehavior() {
        this(Variant.SECONDARY, Size.DEFAULT);
    }

    public ButtonBehavior(Variant variant, Size size) {
        this(Model.of(variant), Model.of(size));
    }

    public Variant getVariant() {
        return variant.getObject();
    }

    public ButtonBehavior setVariant(Variant variant) {
        this.variant.setObject(variant);
        return this;
    }

    public Size getSize() {
        return size.getObject();
    }

    public ButtonBehavior setSize(Size size) {
        this.size.setObject(size);
        return this;
    }

    @Override
    public void onComponentTag(Component component, ComponentTag tag) {
        super.onComponentTag(component, tag);

        Components.assertTag(component, tag, "a", "button", "input");

        if (!component.isEnabledInHierarchy()) {
            tag.put("disabled", "disabled");
        }

        Variant variant = getVariant();
        if (!Variant.NONE.equals(variant) && !Variant.NAV_LINK.equals(variant)) {
            Size size = getSize();
            onComponentTag(component, tag, variant.getCssClassName(), size.getCssClassName());
        } else {
            Attributes.addClass(tag, variant.getCssClassName());
        }
    }

    private void onComponentTag(Component component, ComponentTag tag, String... cssClasses) {
        CssClassNames.Builder builder = CssClassNames.builder().add(
            "btn", (component.isEnabledInHierarchy() ? "" : "disabled"));

        if (!component.isEnabled() && Components.hasTagName(tag, Set.of("a"))) {
            builder.add("disabled");
            Attributes.set("aria-disabled", "true", tag);
        }

        for (String cssClass : cssClasses) {
            builder.add(cssClass);
        }

        Attributes.addClass(tag, builder.toString());
    }

    @Override
    public void detach(Component component) {
        variant.detach();
        size.detach();
    }
}
