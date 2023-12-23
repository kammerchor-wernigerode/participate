package de.agilecoders.wicket.extensions.markup.html.bootstrap.form;

import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.Args;

import java.util.Date;

public class DateTextField extends AbstractDateTextField<Date, org.apache.wicket.extensions.markup.html.form.DateTextField, Date, DateTextFieldConfig, DateTextField> {

    public DateTextField(String id, DateTextFieldConfig config) {
        super(new org.apache.wicket.extensions.markup.html.form.DateTextField(id, Args
            .notNull(config, "config")
            .getFormat()), Date.class, config);
    }

    public DateTextField(String id, IModel<Date> model, DateTextFieldConfig config) {
        super(new org.apache.wicket.extensions.markup.html.form.DateTextField(id, model, Args
            .notNull(config, "config")
            .getFormat()), Date.class, config);
    }
}
