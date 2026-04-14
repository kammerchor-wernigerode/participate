package de.kammerchorwernigerode.app.participate.wicket.markup.html.form;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;

public class TextFormControl extends FormControl<String> {

    public TextFormControl(String id, IModel<String> model) {
        super(id, model);
    }

    @Override
    protected FormComponent<String> createFormComponent(String wicketId) {
        return new TextField<>(wicketId, getModel()) {

            @Override
            protected void onComponentTag(ComponentTag tag) {
                tag.put("type", "text");
                super.onComponentTag(tag);
            }
        };
    }
}
