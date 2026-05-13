package de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.badge;

import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.helper.Texts.BackgroundColor;
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
public class BadgeBehavior extends Behavior {

    private final IModel<BackgroundColor> backgroundColor;
    private final IModel<Boolean> rounded;

    public BadgeBehavior() {
        this(BackgroundColor.SECONDARY, false);
    }

    public BadgeBehavior(BackgroundColor backgroundColor, boolean rounded) {
        this(Model.of(backgroundColor), Model.of(false));
    }

    public BackgroundColor getBackgroundColor() {
        return backgroundColor.getObject();
    }

    public BadgeBehavior setBackgroundColor(BackgroundColor backgroundColor) {
        this.backgroundColor.setObject(backgroundColor);
        return this;
    }

    public boolean isRounded() {
        return rounded.getObject();
    }

    public BadgeBehavior setRounded(boolean rounded) {
        this.rounded.setObject(rounded);
        return this;
    }

    @Override
    public void onComponentTag(Component component, ComponentTag tag) {
        super.onComponentTag(component, tag);

        Components.assertTag(component, tag, "span");

        CssClassNames.Builder builder = CssClassNames.builder()
            .add("badge")
            .add(backgroundColor.map(BackgroundColor::getCssClassName).getObject())
            .add(rounded.getObject() ? "rounded-pill" : "");

        Attributes.addClass(tag, builder.toString());
    }
}
