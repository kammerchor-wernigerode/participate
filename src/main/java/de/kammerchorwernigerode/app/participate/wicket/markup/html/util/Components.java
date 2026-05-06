package de.kammerchorwernigerode.app.participate.wicket.markup.html.util;

import org.apache.wicket.Component;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.IMarkupFragment;
import org.apache.wicket.markup.MarkupException;

import java.util.Set;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Components {

    public static void assertTag(Component component, ComponentTag tag, String... tagNames) {
        assertTag(component, tag, Set.of(tagNames));
    }

    public static void assertTag(Component component, ComponentTag tag, Set<? extends String> tagNames) {
        if (!hasTagName(tag, tagNames)) {
            throw createMarkupException(component, tag, tagNames);
        }
    }

    public static boolean hasTagName(ComponentTag tag, Set<? extends String> tagNames) {
        if (null == tagNames) {
            return false;
        }

        for (String tagName : tagNames) {
            if (tag.getName().equalsIgnoreCase(tagName)) {
                return true;
            }
        }

        return false;
    }

    private static MarkupException createMarkupException(Component component, ComponentTag tag,
                                                         Set<? extends String> tagNames) {
        String msg = String.format("Component [%s] (path = [%s]) must be applied to a tag of type [%s], not: %s",
            component.getId(), component.getPath(), String.join(",", tagNames), tag.toUserDebugString());

        IMarkupFragment markup = component.getMarkup();
        throw new MarkupException(markup.getMarkupResourceStream(), msg);
    }
}
