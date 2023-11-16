package de.vinado.app.participate.wicket.form;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.SimpleFormComponentLabel;

public class FormComponentLabel extends SimpleFormComponentLabel {

    private final FormComponent<?> formComponent;

    public FormComponentLabel(String id, FormComponent<?> formComponent) {
        super(id, formComponent);

        this.formComponent = formComponent;
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
