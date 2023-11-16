package de.vinado.wicket.participate.components.snackbar;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;

/**
 * A simple feedback which shows up, when you append it on a {@link org.apache.wicket.ajax.AjaxRequestTarget}.<br>
 * Change the config with {@link SnackbarConfig}.
 *
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 * @see org.apache.wicket.ajax.AjaxRequestTarget#appendJavaScript(CharSequence)
 * @see org.apache.wicket.ajax.AjaxRequestTarget#prependJavaScript(CharSequence)
 */
public class Snackbar {

    /**
     * {@link SnackbarConfig}
     */
    private static SnackbarConfig snackbarConfig = new SnackbarConfig()
        .withShowAction(false);

    public static void show(AjaxRequestTarget target, String content) {
        String json = snackbarConfig.withText(content).toJsonString();
        target.appendJavaScript("Snackbar.show(" + json + ");");
    }

    public static void show(AjaxRequestTarget target, IModel<String> contentModel) {
        String json = snackbarConfig.withText(contentModel.getObject()).toJsonString();
        target.appendJavaScript("Snackbar.show(" + json + ");");
    }

    /**
     * Sets a custom {@link SnackbarConfig}
     *
     * @param snackbarConfig Snackbar configuration
     * @return Returns itself
     */
    public Snackbar withConfig(SnackbarConfig snackbarConfig) {
        Snackbar.snackbarConfig = snackbarConfig;
        return this;
    }
}
