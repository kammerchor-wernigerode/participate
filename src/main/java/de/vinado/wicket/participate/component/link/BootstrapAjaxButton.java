package de.vinado.wicket.participate.component.link;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class BootstrapAjaxButton extends de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton {

    public BootstrapAjaxButton(final String componentId, final Buttons.Type type) {
        super(componentId, type);
    }

    public BootstrapAjaxButton(final String componentId, final IModel<String> model, final Buttons.Type type) {
        super(componentId, model, type);
    }

    public BootstrapAjaxButton(final String id, final Form<?> form, final Buttons.Type type) {
        super(id, form, type);
    }

    public BootstrapAjaxButton(final String id, final IModel<String> model, final Form<?> form, final Buttons.Type type) {
        super(id, model, form, type);
    }

    @Override
    protected void onError(final AjaxRequestTarget target, final Form<?> form) {
        if (null == getFeedbackPanel()) {
            return;
        }

        target.add(getFeedbackPanel());
        form.visitFormComponents(new IVisitor<FormComponent<?>, Object>() {
            @Override
            public void component(final FormComponent<?> components, final IVisit<Object> iVisit) {
                if (!components.getRenderBodyOnly()) {
                    target.add(components);
                }
            }
        });
    }

    protected FeedbackPanel getFeedbackPanel() {
        return null;
    }
}
