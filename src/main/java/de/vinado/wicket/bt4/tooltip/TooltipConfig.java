package de.vinado.wicket.bt4.tooltip;

import de.agilecoders.wicket.jquery.IKey;

import java.time.Duration;

import static de.vinado.wicket.bt4.tooltip.TooltipConfig.Boundary.scrollParent;

public class TooltipConfig extends de.agilecoders.wicket.core.markup.html.bootstrap.components.TooltipConfig {

    private static final IKey<String> Container = newKey("container", Boolean.FALSE.toString());
    private static final IKey<Long> Offset = newKey("offset", 0L);
    private static final IKey<String> Boundary = newKey("boundary", scrollParent.name());

    public TooltipConfig withContainer(String container) {
        put(Container, container);
        return this;
    }

    public TooltipConfig withOffset(Long offset) {
        put(Offset, offset);
        return this;
    }

    public TooltipConfig withBoundary(Boundary boundary) {
        put(Boundary, boundary.name());
        return this;
    }

    @Override
    public TooltipConfig withAnimation(boolean value) {
        return (TooltipConfig) super.withAnimation(value);
    }

    @Override
    public TooltipConfig withPlacement(IPlacement value) {
        return (TooltipConfig) super.withPlacement(value);
    }

    @Override
    public TooltipConfig withSelector(String value) {
        return (TooltipConfig) super.withSelector(value);
    }

    @Override
    public TooltipConfig withTitle(String value) {
        return (TooltipConfig) super.withTitle(value);
    }

    @Override
    public TooltipConfig withContent(String value) {
        return (TooltipConfig) super.withContent(value);
    }

    @Override
    public TooltipConfig withTrigger(OpenTrigger value) {
        return (TooltipConfig) super.withTrigger(value);
    }

    @Override
    public TooltipConfig withDelay(Duration value) {
        return (TooltipConfig) super.withDelay(value);
    }

    @Override
    public TooltipConfig withHtml(boolean value) {
        return (TooltipConfig) super.withHtml(value);
    }

    @Override
    public TooltipConfig withSanitizer(boolean value) {
        return (TooltipConfig) super.withSanitizer(value);
    }


    public enum Boundary {
        scrollParent,
        viewport,
        window,
        ;
    }
}
