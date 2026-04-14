package de.kammerchorwernigerode.app.participate.wicket.markup.html.form;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.util.visit.IVisitor;

public class BootstrapForm<T> extends Form<T> {

    public BootstrapForm(String id, IModel<T> model) {
        super(id, model);
    }

    @Override
    protected void onValidate() {
        RequestCycle.get().find(AjaxRequestTarget.class).ifPresent(target -> {
            IVisitor<FormComponent<?>, Void> visitor = visitor(target);
            visitFormComponents(visitor);
        });
    }

    protected IVisitor<FormComponent<?>, Void> visitor(AjaxRequestTarget target) {
        return (fc, visit) -> {
            if (fc.getOutputMarkupId() && (fc.isRequired() || !fc.getValidators().isEmpty())) {
                target.add(fc);
            }
        };
    }
}
