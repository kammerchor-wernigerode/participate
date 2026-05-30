package de.kammerchorwernigerode.app.participate.wicket.markup.html.form;

import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.form.DropDownChoiceBehavior.Size;
import org.apache.wicket.model.IModel;

import lombok.Getter;

public abstract class FormSelect<T> extends BootstrapFormComponent<T, T> {

    @Getter
    private Size size = Size.DEFAULT;

    public FormSelect(String id, IModel<T> model) {
        super(id, model);
    }

    public FormSelect<T> setSize(Size size) {
        this.size = size;
        return this;
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
        return "form-select";
    }
}
