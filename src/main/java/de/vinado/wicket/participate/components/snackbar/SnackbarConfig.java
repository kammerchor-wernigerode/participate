package de.vinado.wicket.participate.components.snackbar;

import de.agilecoders.wicket.jquery.AbstractConfig;
import de.agilecoders.wicket.jquery.IKey;

public class SnackbarConfig extends AbstractConfig {

    private final IKey<String> text = newKey("text", "");
    private final IKey<Boolean> showAction = newKey("showAction", true);

    public SnackbarConfig withText(String value) {
        put(text, value);
        return this;
    }

    public SnackbarConfig withShowAction(boolean value) {
        put(showAction, value);
        return this;
    }
}
