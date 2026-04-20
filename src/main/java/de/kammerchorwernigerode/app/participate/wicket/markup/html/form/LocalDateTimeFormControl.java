package de.kammerchorwernigerode.app.participate.wicket.markup.html.form;

import de.kammerchorwernigerode.app.participate.wicket.markup.html.form.datetime.DateTimeLocalTextField;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;

import java.time.LocalDateTime;

public class LocalDateTimeFormControl extends FormControl<LocalDateTime> {

    public LocalDateTimeFormControl(String id, IModel<LocalDateTime> model) {
        super(id, model);
    }

    @Override
    protected FormComponent<LocalDateTime> createFormComponent(String wicketId) {
        return new DateTimeLocalTextField(wicketId, getModel()) {


            @Override
            protected void onComponentTag(ComponentTag tag) {
                tag.put("type", "datetime-local");
                super.onComponentTag(tag);
            }
        };
    }
}
