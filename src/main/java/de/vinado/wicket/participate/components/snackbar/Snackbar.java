package de.vinado.wicket.participate.components.snackbar;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;

public class Snackbar {

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

    public Snackbar withConfig(SnackbarConfig snackbarConfig) {
        Snackbar.snackbarConfig = snackbarConfig;
        return this;
    }
}
