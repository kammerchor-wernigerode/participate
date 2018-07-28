package de.vinado.wicket.participate.components;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.ICssClassNameProvider;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public enum TextAlign implements ICssClassNameProvider {
    LEFT("text-left"),
    CENTER("text-center"),
    RIGHT("text-right");

    /**
     * Css class name
     */
    private final String cssClassName;

    /**
     * Construct.
     *
     * @param cssClassName Css class name
     */
    TextAlign(final String cssClassName) {
        this.cssClassName = cssClassName;
    }

    /**
     * @return css class
     * @see #toString()
     */
    @Override
    public String cssClassName() {
        return cssClassName;
    }
}
