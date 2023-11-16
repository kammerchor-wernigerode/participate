package de.vinado.wicket.participate.components;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.ICssClassNameProvider;

public enum TextAlign implements ICssClassNameProvider {
    LEFT("text-left"),
    CENTER("text-center"),
    RIGHT("text-right");

    private final String cssClassName;

    TextAlign(String cssClassName) {
        this.cssClassName = cssClassName;
    }

    @Override
    public String cssClassName() {
        return cssClassName;
    }
}
