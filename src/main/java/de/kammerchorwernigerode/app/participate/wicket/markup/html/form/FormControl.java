package de.kammerchorwernigerode.app.participate.wicket.markup.html.form;

import org.apache.wicket.model.IModel;

public abstract class FormControl<T> extends BootstrapFormComponent<T, T> {

    public FormControl(String id, IModel<T> model) {
        super(id, model);
    }

    @Override
    public void convertInput() {
        setConvertedInput(getFormComponent().getConvertedInput());
    }

    @Override
    protected void onBeforeRender() {
        super.onBeforeRender();

        getFormComponent().setModelObject(getModelObject());
    }

    @Override
    protected String getCssClassName() {
        return "form-control";
    }
}
