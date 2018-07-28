package de.vinado.wicket.participate.components.snackbar;

import de.agilecoders.wicket.jquery.AbstractConfig;
import de.agilecoders.wicket.jquery.IKey;
import org.apache.wicket.util.lang.Args;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class SnackbarConfig extends AbstractConfig {

    private IKey<String> content = newKey("content", "");
    private IKey<Style> style = newKey("style", Style.snackbar);
    private IKey<Integer> timeout = newKey("timeout", 0);

    public SnackbarConfig() {
    }

    public SnackbarConfig(final SnackbarConfig copy) {
        Args.notNull(copy, "copy");
        withContent(copy.get(content));
        withStyle(copy.get(style));
        withTimeout(copy.get(timeout));
    }

    public SnackbarConfig withContent(final String value) {
        put(content, value);
        return this;
    }

    public SnackbarConfig withStyle(final Style value) {
        put(style, value);
        return this;
    }

    public SnackbarConfig withTimeout(final Integer value) {
        put(timeout, value);
        return this;
    }

    public enum Style {
        snackbar, toast
    }
}
