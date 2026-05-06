package de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.form;

import de.kammerchorwernigerode.app.participate.wicket.markup.html.util.Attributes;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.util.Components;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;

public class CheckBoxBehavior extends Behavior {

    @Override
    public void onComponentTag(Component component, ComponentTag tag) {
        super.onComponentTag(component, tag);

        Components.assertTag(component, tag, "input");
        tag.put("type", "checkbox");
        Attributes.addClass(tag, "form-check-input");
    }
}
