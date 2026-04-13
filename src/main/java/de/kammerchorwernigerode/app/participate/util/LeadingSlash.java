package de.kammerchorwernigerode.app.participate.util;

import java.util.function.Function;
import java.util.function.UnaryOperator;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum LeadingSlash {

    ABSENT(input -> input.replaceFirst("^/", "")),
    PRESENT(input -> "/" + ABSENT.modify(input)),
    ;

    private final UnaryOperator<String> modification;

    public String modify(@NonNull String input) {
        return modification.apply(input);
    }

    public static Function<String, String> ensure(@NonNull LeadingSlash presence) {
        return input -> ensure(input, presence);
    }

    public static String ensure(@NonNull String input, @NonNull LeadingSlash presence) {
        return presence.modify(input);
    }
}
