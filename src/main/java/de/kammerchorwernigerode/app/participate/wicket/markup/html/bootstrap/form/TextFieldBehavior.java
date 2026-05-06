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
public class TextFieldBehavior extends Behavior {

    @Getter
    private Size size = Size.DEFAULT;

    public TextFieldBehavior setSize(Size size) {
        this.size = size;
        return this;
    }

    @Override
    public void onComponentTag(Component component, ComponentTag tag) {
        super.onComponentTag(component, tag);

        Components.assertTag(component, tag, "input");
        Attributes.addClass(tag, "form-control", size.getCssClassName());
    }


    @RequiredArgsConstructor(access = AccessLevel.PACKAGE)
    public enum Size {

        DEFAULT(""),
        SMALL("form-control-sm"),
        LARGE("form-control-lg"),
        ;

        @Getter
        private final String cssClassName;
    }
}
