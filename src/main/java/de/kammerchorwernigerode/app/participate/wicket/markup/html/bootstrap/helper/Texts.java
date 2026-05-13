package de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.helper;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Texts {


    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public enum BackgroundColor {

        PRIMARY("text-bg-primary"),
        SECONDARY("text-bg-secondary"),
        SUCCESS("text-bg-success"),
        DANGER("text-bg-danger"),
        WARNING("text-bg-warning"),
        INFO("text-bg-info"),
        LIGHT("text-bg-light"),
        DARK("text-bg-dark"),
        NONE(""),
        ;

        @Getter
        private final String cssClassName;
    }

}
