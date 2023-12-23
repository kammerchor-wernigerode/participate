package de.vinado.app.participate.wicket.bt5.tooltip;

import de.agilecoders.wicket.jquery.IKey;
import de.agilecoders.wicket.jquery.util.Json;

import java.time.Duration;

public class TooltipConfig extends de.agilecoders.wicket.core.markup.html.bootstrap.components.TooltipConfig {

    private static final IKey<Json.RawValue> Boundary = newKey("boundary", new Json.RawValue("'clippingParents'"));

    public TooltipConfig withBoundary(Json.RawValue boundary) {
        put(Boundary, boundary);
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
}
