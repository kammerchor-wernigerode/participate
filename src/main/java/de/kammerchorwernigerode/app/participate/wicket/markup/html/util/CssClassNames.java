package de.kammerchorwernigerode.app.participate.wicket.markup.html.util;

import org.apache.wicket.util.string.Strings;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CssClassNames {

    public static Set<String> split(String cssClassName) {
        String[] cssClasses = Optional.ofNullable(cssClassName)
            .filter(self -> !Strings.isEmpty(self))
            .map(self -> self.split("\\s+"))
            .orElseGet(() -> new String[0]);

        return Arrays.stream(cssClasses)
            .filter(cssClass -> !Strings.isEmpty(cssClass))
            .collect(Collectors.toSet());
    }

    public static String join(Iterable<String> cssClasses) {
        return String.join(" ", cssClasses);
    }

    public static Builder parse(String cssClassName) {
        return new Builder(cssClassName);
    }

    public static Builder builder() {
        return new Builder("");
    }


    @RequiredArgsConstructor
    public static class Builder {

        private final Set<String> cssClasses = new LinkedHashSet<>();

        public Builder(String cssClassName) {
            addRaw(cssClassName);
        }

        public Builder addRaw(String cssClassName) {
            Set<String> cssClasses = split(cssClassName);
            return add(cssClasses);
        }

        public Builder add(String... cssClasses) {
            Set<String> cssClassSet = Set.of(cssClasses);
            return add(cssClassSet);
        }

        public Builder add(Set<String> cssClasses) {
            this.cssClasses.addAll(cssClasses);
            return this;
        }

        @Override
        public String toString() {
            return join(cssClasses);
        }
    }
}
