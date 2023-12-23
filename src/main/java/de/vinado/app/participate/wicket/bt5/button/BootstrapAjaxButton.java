package de.vinado.app.participate.wicket.bt5.button;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;

public class BootstrapAjaxButton extends de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton {

    public BootstrapAjaxButton(String componentId, Buttons.Type type) {
        super(componentId, type);
    }

    public BootstrapAjaxButton(String componentId, IModel<String> model, Buttons.Type type) {
        super(componentId, model, type);
    }

    @Override
    protected void onError(AjaxRequestTarget target) {
        super.onError(target);

        FeedbackPanel feedbackPanel = getFeedbackPanel();
        if (null == feedbackPanel) {
            return;
        }

        target.add(feedbackPanel);
        getForm().visitFormComponents((components, iVisit) -> {
            if (!components.getRenderBodyOnly()) target.add(components);
        });
    }

    protected FeedbackPanel getFeedbackPanel() {
        return null;
    }
}
