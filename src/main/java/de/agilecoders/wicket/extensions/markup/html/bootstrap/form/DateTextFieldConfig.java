package de.agilecoders.wicket.extensions.markup.html.bootstrap.form;

import org.apache.wicket.util.string.Strings;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTextFieldConfig extends AbstractDateTextFieldConfig<DateTextFieldConfig, Date> {

    @Override
    public DateTextFieldConfig withStartDate(Date value) {
        put(StartDate, format(value));
        return this;
    }

    @Override
    public DateTextFieldConfig withEndDate(Date value) {
        put(StartDate, format(value));
        return this;
    }

    private String format(Date value) {
        final String format = getFormat();
        if (Strings.isEmpty(format)) {
            return value.toString();
        } else {
            DateFormat formatter = new SimpleDateFormat(format);
            return formatter.format(value);
        }
    }
}
