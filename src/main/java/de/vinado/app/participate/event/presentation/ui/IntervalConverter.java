package de.vinado.app.participate.event.presentation.ui;

import de.vinado.app.participate.event.model.Interval;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.converter.AbstractConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

public class IntervalConverter extends AbstractConverter<Interval> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT);

    @Override
    protected Class<Interval> getTargetType() {
        return Interval.class;
    }

    @Override
    public Interval convertToObject(String value, Locale locale) throws ConversionException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String convertToString(Interval interval, Locale locale) {
        if (null == interval) {
            return null;
        }

        return convert(interval, null == locale ? Locale.getDefault() : locale);
    }

    private String convert(Interval interval, Locale locale) {
        LocalDate start = interval.getStart();
        LocalDate end = interval.getEnd();

        DateTimeFormatter formatter = FORMATTER.withLocale(locale);
        return formatter.withLocale(locale).format(start) + " – " + formatter.withLocale(locale).format(end);
    }
}
