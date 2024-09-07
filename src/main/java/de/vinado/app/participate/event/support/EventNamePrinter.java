package de.vinado.app.participate.event.support;

import de.vinado.app.participate.event.model.EventName;
import de.vinado.app.participate.event.model.Interval;
import org.springframework.format.Printer;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class EventNamePrinter implements Printer<EventName> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MMMM yyyy");

    @Override
    public String print(EventName eventName, Locale locale) {
        DateTimeFormatter formatter = FORMATTER.withLocale(locale);
        String value = eventName.value();
        Interval interval = eventName.interval();
        return value + " " + formatter.format(interval.getStart());
    }
}
