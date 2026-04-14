package de.kammerchorwernigerode.app.participate.wicket.markup.html.form;

import org.apache.wicket.extensions.markup.html.form.datetime.LocalDateTimeTextField;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.util.convert.converter.AbstractJavaTimeConverter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

public class LocalDateTimeFormControl extends FormControl<LocalDateTime> {

    public LocalDateTimeFormControl(String id, IModel<LocalDateTime> model) {
        super(id, model);
    }

    @Override
    protected FormComponent<LocalDateTime> createFormComponent(String wicketId) {
        return new LocalDateTimeTextField(wicketId, getModel(), "yyyy-MM-dd'T'hh:mm") {

            private final LocalDateTimeConverter converter = new LocalDateTimeConverter();

            @Override
            protected void onComponentTag(ComponentTag tag) {
                tag.put("type", "datetime-local");
                super.onComponentTag(tag);
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
        };
    }


    private static class LocalDateTimeConverter extends AbstractJavaTimeConverter<LocalDateTime> {

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
