package de.vinado.wicket.participate.components.forms;

import de.agilecoders.wicket.core.markup.html.bootstrap.form.FormType;
import de.vinado.wicket.participate.behavoirs.decorators.BootstrapFormDecorator;
import de.vinado.wicket.participate.behavoirs.decorators.BootstrapHorizontalFormDecorator;
import de.vinado.wicket.participate.behavoirs.decorators.BootstrapInlineFormDecorator;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.visit.IVisitor;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class BootstrapForm<T> extends de.agilecoders.wicket.core.markup.html.bootstrap.form.BootstrapForm<T> {

    public BootstrapForm(final String componentId) {
        super(componentId);
    }

    public BootstrapForm(final String componentId, final IModel<T> model) {
        super(componentId, model);
    }

    public void addBootstrapFormDecorator() {
        type(FormType.Default);
        visitChildren(FormComponent.class, (IVisitor<FormComponent, Void>) (component, voidIVisit) -> {
            if (!(component instanceof Button) && !(component instanceof CheckGroup) && !(component instanceof RadioGroup)) {
                component.add(BootstrapFormDecorator.decorate());
            }
            voidIVisit.dontGoDeeper();
        });
    }

    public void addBootstrapInlineFormDecorator() {
        type(FormType.Inline);
        visitChildren(FormComponent.class, (IVisitor<FormComponent, Void>) (component, voidIVisit) -> {
            if (!(component instanceof Button) && !(component instanceof CheckGroup) && !(component instanceof RadioGroup)) {
                component.add(BootstrapInlineFormDecorator.decorate());
            }
            voidIVisit.dontGoDeeper();
        });
    }

    public void addBootstrapHorizontalFormDecorator() {
        type(FormType.Default);
        visitChildren(FormComponent.class, (IVisitor<FormComponent, Void>) (component, voidIVisit) -> {
            if (!(component instanceof Button)) {
                component.add(BootstrapHorizontalFormDecorator.decorate());
            }
            voidIVisit.dontGoDeeper();
        });
    }
}
