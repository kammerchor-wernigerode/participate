package de.kammerchorwernigerode.app.participate.wicket.markup.html.util;

import de.kammerchorwernigerode.app.participate.wicket.markup.html.util.CssClassNames.Builder;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.util.string.Strings;

import java.util.Set;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Attributes {

    public static void addClass(ComponentTag tag, String... cssClasses) {
        addClass(tag, Set.of(cssClasses));
    }

    public static void addClass(ComponentTag tag, Set<String> cssClasses) {
        String classAttribute = tag.getAttribute("class");
        Builder builder = CssClassNames.parse(classAttribute)
            .add(cssClasses);

        set("class", builder.toString(), tag);
    }

    public static void set(String key, String value, ComponentTag tag) {
        if (Strings.isEmpty(value)) {
            tag.remove(key);
        } else {
            tag.put(key, value);
        }
    }
}
