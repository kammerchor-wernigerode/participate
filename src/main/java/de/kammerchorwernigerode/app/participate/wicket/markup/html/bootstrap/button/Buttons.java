package de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.button;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Buttons {


    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public enum Variant {

        PRIMARY("btn-primary"),
        SECONDARY("btn-secondary"),
        SUCCESS("btn-success"),
        DANGER("btn-danger"),
        WARNING("btn-warning"),
        INFO("btn-info"),
        LIGHT("btn-light"),
        DARK("btn-dark"),
        LINK("btn-link"),

        OUTLINE_PRIMARY("btn-outline-primary"),
        OUTLINE_SECONDARY("btn-outline-secondary"),
        OUTLINE_SUCCESS("btn-outline-success"),
        OUTLINE_DANGER("btn-outline-danger"),
        OUTLINE_WARNING("btn-outline-warning"),
        OUTLINE_INFO("btn-outline-info"),
        OUTLINE_LIGHT("btn-outline-light"),
        OUTLINE_DARK("btn-outline-dark"),

        NAV_LINK("nav-link"),
        NONE(""),
        ;

        @Getter
        private final String cssClassName;
    }

    @RequiredArgsConstructor
    public enum Size {

        SMALL("btn-sm"),
        DEFAULT(""),
        LARGE("btn-lg"),
        ;

        @Getter
        private final String cssClassName;
    }
}
