package de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.icon;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FaBuilder {

    private final Fa.Graphic graphic;
    private Fa.Rotation rotation;
    private Fa.Size size;
    private boolean spin;
    private boolean fixedWidth;

    public FaBuilder rotation(Fa.Rotation rotation) {
        this.rotation = rotation;
        return this;
    }

    public FaBuilder size(Fa.Size size) {
        this.size = size;
        return this;
    }

    public FaBuilder spin() {
        this.spin = true;
        return this;
    }

    public FaBuilder fw() {
        this.fixedWidth = true;
        return this;
    }

    public Fa build() {
        List<String> cssClassNames = new ArrayList<>();

        cssClassNames.add(graphic.getPrefix());
        cssClassNames.add(graphic.getName().replace("_", "-"));

        if (null != rotation) {
            cssClassNames.add("fa-" + rotation.name().replace("_", "-"));
        }

        if (null != size) {
            cssClassNames.add(size.getStyle());
        }

        if (spin) {
            cssClassNames.add("fa-spin");
        }

        if (fixedWidth) {
            cssClassNames.add("fa-fw");
        }

        return new Fa(cssClassNames.toArray(new String[0]));
    }

    public static FaBuilder fa(Fa.Graphic graphic) {
        return new FaBuilder(graphic);
    }
}
