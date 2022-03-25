package de.vinado.wicket.bt4.button;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;

/**
 * @author Vincent Nadoll
 */
public class BootstrapAjaxButton extends de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton {

    private static final long serialVersionUID = -2401274183769762849L;

    public BootstrapAjaxButton(String componentId, Buttons.Type type) {
        super(componentId, type);
    }

    public BootstrapAjaxButton(String componentId, IModel<String> model, Buttons.Type type) {
        super(componentId, model, type);
    }

    public BootstrapAjaxButton(String id, Form<?> form, Buttons.Type type) {
        super(id, form, type);
    }

    public BootstrapAjaxButton(String id, IModel<String> model, Form<?> form, Buttons.Type type) {
        super(id, model, form, type);
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
