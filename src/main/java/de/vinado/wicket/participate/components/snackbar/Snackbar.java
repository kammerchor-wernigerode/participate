package de.vinado.wicket.participate.components.snackbar;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

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
    private static SnackbarConfig snackbarConfig = new SnackbarConfig();

    /**
     * @param content Content of the Snackbar
     * @return {@link CharSequence} JavaScript function
     */
    @Deprecated
    public static CharSequence show(final String content) {
        return show(Model.of(content));
    }

    /**
     * @param contentModel Content of the Snackbar
     * @return {@link CharSequence} JavaScript function
     */
    @Deprecated
    public static CharSequence show(final IModel<String> contentModel) {
        return "$.snackbar(" + snackbarConfig.withContent(contentModel.getObject()).toJsonString() + ");";
    }

    public static void show(final AjaxRequestTarget target, final String content) {
        target.appendJavaScript("$.snackbar(" + snackbarConfig.withContent(content).toJsonString() + ");");
    }

    public static void show(final AjaxRequestTarget target, final IModel<String> contentModel) {
        target.appendJavaScript("$.snackbar(" + snackbarConfig.withContent(contentModel.getObject()).toJsonString() + ");");
    }

    /**
     * Sets a custom {@link SnackbarConfig}
     *
     * @param snackbarConfig Snackbar configuration
     * @return Returns itself
     */
    public Snackbar withConfig(final SnackbarConfig snackbarConfig) {
        Snackbar.snackbarConfig = snackbarConfig;
        return this;
    }
}
