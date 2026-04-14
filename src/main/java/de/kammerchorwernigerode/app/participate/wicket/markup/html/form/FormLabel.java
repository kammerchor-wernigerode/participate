package de.kammerchorwernigerode.app.participate.wicket.markup.html.form;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.MarkupStream;
import org.apache.wicket.markup.html.WebComponent;
import org.apache.wicket.markup.html.form.FormComponent;

public class FormLabel extends WebComponent {

    private final FormComponent<?> formComponent;

    private FormComponent<?> reference;

    public FormLabel(String id, FormComponent<?> formComponent) {
        super(id);
        this.formComponent = formComponent;
        this.reference = formComponent;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        reference.setOutputMarkupId(true);
        setDefaultModel(formComponent.getLabel());
    }

    @Override
    protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);

        checkComponentTag(tag, "label");
        tag.put("for", reference.getMarkupId());
    }

    @Override
    public void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {
        String label = getDefaultModelObjectAsString();

        if (formComponent.isRequired()) {
            label += " *";
        }

        replaceComponentTagBody(markupStream, openTag, label);
    }

    public FormLabel references(FormComponent<?> formComponent) {
        this.reference = formComponent;
        return this;
    }
}
