package de.vinado.wicket.participate.components;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.ICssClassNameProvider;

public enum TextAlign implements ICssClassNameProvider {
    START("text-start"),
    CENTER("text-center"),
    END("text-end");

    private final String cssClassName;

    TextAlign(String cssClassName) {
        this.cssClassName = cssClassName;
    }

    @Override
    public String cssClassName() {
        return cssClassName;
    }
}
