package de.vinado.wicket.participate.behavoirs;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.time.Duration;


/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class SnackbarBehavior extends Behavior {

    private IModel<String> textModel;
    private Style style = Style.SNACKBAR;
    private Duration duration = Duration.NONE;

    public SnackbarBehavior(final IModel<String> textModel) {
        super();
        this.textModel = textModel;
    }

    public SnackbarBehavior(final String text) {
        this(Model.of(text));
    }

    @Override
    public void onComponentTag(Component component, ComponentTag tag) {
        tag.put("data-toggle", "snackbar");
        tag.put("data-content", textModel.getObject());
        tag.put("data-timeout", String.valueOf(duration.getMilliseconds()));
        tag.put("data-style", style.toString());
    }

    public SnackbarBehavior withDuration(final Duration duration) {
        this.duration = duration;
        return this;
    }

    public SnackbarBehavior withStyle(final Style style) {
        this.style = style;
        return this;
    }

    public enum Style {
        SNACKBAR("snackbar"),
        TOAST("toast");

        /**
         * Css class name
         */
        private final String style;

        /**
         * Construct.
         *
         * @param style Style
         */
        Style(final String style) {
            this.style = style;
        }

        /**
         * @return class
         */
        public String toString() {
            return style;
        }
    }
}
