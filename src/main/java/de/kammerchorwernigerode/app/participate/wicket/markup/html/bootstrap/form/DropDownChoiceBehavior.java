package de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.form;

import de.kammerchorwernigerode.app.participate.wicket.markup.html.util.Attributes;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.util.Components;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class DropDownChoiceBehavior extends Behavior {

    @Getter
    private Size size = Size.DEFAULT;

    public DropDownChoiceBehavior setSize(Size size) {
        this.size = size;
        return this;
    }

    @Override
    public void onComponentTag(Component component, ComponentTag tag) {
        super.onComponentTag(component, tag);

        Components.assertTag(component, tag, "select");
        Attributes.addClass(tag, "form-select", size.getCssClassName());
    }


    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    public enum Size {

        DEFAULT(""),
        SMALL("form-select-sm"),
        LARGE("form-select-lg"),
        ;

        @Getter
        private final String cssClassName;
    }
}
