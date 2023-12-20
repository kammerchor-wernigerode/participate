package de.vinado.app.participate.wicket.form;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;

public class FormComponentLabel extends org.apache.wicket.markup.html.form.FormComponentLabel {

    private final FormComponent<?> formComponent;

    public FormComponentLabel(String id, FormComponent<?> formComponent) {
        super(id, formComponent);

        this.formComponent = formComponent;
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();

        IModel<String> label = formComponent.getLabel();
        if (null == label) {
            throw new IllegalStateException("Provided form component does not have a label set. "
                + "Use FormComponent.setLabel(IModel) to set the model "
                + "that will feed this label");
        }

        setDefaultModel(label);
    }

    @Override
    public void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {
        StringBuilder builder = new StringBuilder();
        builder.append(getDefaultModelObjectAsString());

        if (formComponent.isRequired()) {
            builder.append(" *");
        }

        replaceComponentTagBody(markupStream, openTag, builder.toString());
    }
}
