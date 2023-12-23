package de.vinado.app.participate.wicket.bt5.form;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.tempusdominus.TempusDominusBehavior;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.tempusdominus.TempusDominusConfig;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.tempusdominus.TempusDominusDisplayConfig;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.tempusdominus.TempusDominusLocalizationConfig.DateFormatType;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.util.convert.converter.DateConverter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateTimeTextField extends TextField<Date> {

    private final TempusDominusConfig config;
    private final TempusDominusConverter converter;

    public DateTimeTextField(String id, IModel<Date> model, TempusDominusConfig config) {
        super(id, model, Date.class);
        this.config = config;
        this.converter = new TempusDominusConverter();
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        config
            .withLocalization(localization -> localization
                .withDateFormat(DateFormatType.L, getPattern(converter.getDateFormat(getLocale())))
                .withFormat(DateFormatType.L.name()))
            .withDisplay(display -> display
                .withTheme(TempusDominusDisplayConfig.ThemeType.LIGHT)
                .withButton(TempusDominusDisplayConfig.ButtonType.TODAY, true)
                .withButton(TempusDominusDisplayConfig.ButtonType.CLEAR, true)
                .withButton(TempusDominusDisplayConfig.ButtonType.CLOSE, true))
        ;

        add(new TempusDominusBehavior(config));
    }

    private static String getPattern(DateFormat format) {
        return ((SimpleDateFormat) format).toLocalizedPattern();
    }

    @Override
    protected IConverter<?> createConverter(Class<?> type) {
        if (type.isAssignableFrom(Date.class)) {
            return converter;
        }
        return null;
    }


    private static class TempusDominusConverter extends DateConverter {

        @Override
        public DateFormat getDateFormat(Locale locale) {
            return SimpleDateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, locale);
        }
    }
}
