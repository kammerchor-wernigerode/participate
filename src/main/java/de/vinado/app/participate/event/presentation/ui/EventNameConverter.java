package de.vinado.app.participate.event.presentation.ui;

import de.vinado.app.participate.event.model.EventName;
import de.vinado.app.participate.event.support.EventNamePrinter;
import org.apache.wicket.util.convert.ConversionException;
import org.apache.wicket.util.convert.converter.AbstractConverter;
import org.springframework.format.Printer;

import java.util.Locale;

public class EventNameConverter extends AbstractConverter<EventName> {

    private final Printer<EventName> printer = new EventNamePrinter();

    @Override
    protected Class<EventName> getTargetType() {
        return EventName.class;
    }

    @Override
    public EventName convertToObject(String value, Locale locale) throws ConversionException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String convertToString(EventName value, Locale locale) {
        if (null == value) {
            return null;
        }

        return printer.print(value, null == locale ? Locale.getDefault() : locale);
    }
}
