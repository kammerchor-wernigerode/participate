package de.vinado.wicket.participate.components.snackbar;

import de.agilecoders.wicket.jquery.AbstractConfig;
import de.agilecoders.wicket.jquery.IKey;
import org.apache.wicket.util.lang.Args;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class SnackbarConfig extends AbstractConfig {

    private final IKey<String> text = newKey("text", "");
    private final IKey<Integer> duration = newKey("duration", -1);
    private final IKey<Boolean> showAction = newKey("showAction", true);

    public SnackbarConfig() {
    }

    public SnackbarConfig(SnackbarConfig copy) {
        Args.notNull(copy, "copy");
        withText(copy.get(text));
        withDuration(copy.get(duration));
        withShowAction(copy.get(showAction));
    }

    public SnackbarConfig withText(String value) {
        put(text, value);
        return this;
    }

    public SnackbarConfig withDuration(int value) {
        put(duration, value);
        return this;
    }

    public SnackbarConfig withShowAction(boolean value) {
        put(showAction, value);
        return this;
    }
}
