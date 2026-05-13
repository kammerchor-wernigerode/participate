package de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.icon;

import de.kammerchorwernigerode.app.participate.wicket.markup.html.image.IconType;

import lombok.Getter;

public class Fa extends IconType {

    public Fa(String... cssClassNames) {
        super(String.join(" ", cssClassNames));
    }

    @Override
    public String cssClassName() {
        return getCssClassName();
    }


    public interface Graphic {

        String getPrefix();

        String getName();
    }

    public enum Solid implements Graphic {

        car,
        ;

        @Override
        public String getPrefix() {
            return "fa-solid";
        }

        @Override
        public String getName() {
            return "fa-" + name();
        }
    }

    public enum Rotation {

        flip_horizontal,
        flip_vertical,
        normal,
        rotate_180,
        rotate_270,
        rotate_90,
        ;
    }

    public enum Size {

        two("2x"),
        three("3x"),
        four("4x"),
        five("5x"),
        large("lg"),
        ;

        @Getter
        private final String style;

        Size(final String factor) {
            this.style = "fa-" + factor;
        }
    }
}
