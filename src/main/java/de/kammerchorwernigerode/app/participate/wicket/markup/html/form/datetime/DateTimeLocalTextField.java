package de.kammerchorwernigerode.app.participate.wicket.markup.html.form.datetime;

import org.apache.wicket.extensions.markup.html.form.datetime.LocalDateTimeTextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.util.convert.converter.AbstractJavaTimeConverter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

public class DateTimeLocalTextField extends LocalDateTimeTextField {

    private final Converter converter = new Converter();

    public DateTimeLocalTextField(String id, IModel<LocalDateTime> model) {
        super(id, model, "yyyy-MM-dd'T'hh:mm");
    }

    @Override
    protected String[] getInputTypes() {
        return new String[]{"datetime-local"};
    }

    @Override
    protected IConverter<?> createConverter(Class<?> clazz) {
        if (LocalDateTime.class.isAssignableFrom(clazz)) {
            return converter;
        }

        return super.createConverter(clazz);
    }


    public static class Converter extends AbstractJavaTimeConverter<LocalDateTime> {

        @Override
        protected LocalDateTime createTemporal(TemporalAccessor temporalAccessor) {
            return LocalDateTime.from(temporalAccessor);
        }

        @Override
        protected DateTimeFormatter getDateTimeFormatter() {
            return DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        }

        @Override
        protected Class<LocalDateTime> getTargetType() {
            return LocalDateTime.class;
        }
    }
}
