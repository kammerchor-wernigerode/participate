package de.kammerchorwernigerode.app.participate.event.presentation;

import de.kammerchorwernigerode.app.participate.event.presentation.model.EventProjection;
import org.springframework.format.Printer;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public abstract class EventPeriodPrinter implements Printer<EventProjection> {

    @Override
    public String print(EventProjection event, Locale locale) {
        DateTimeFormatter formatter = getFormatter().localizedBy(locale);
        DateTimeFormatter startFormatter = formatter.withZone(event.getStartZoneId());
        DateTimeFormatter endFormatter = formatter.withZone(event.getEndZoneId());

        ZonedDateTime startDateTime = event.getStartDateTime();
        ZonedDateTime endDateTime = event.getEndDateTime();
        return startFormatter.format(startDateTime) + "–" + endFormatter.format(endDateTime);
    }

    protected abstract DateTimeFormatter getFormatter();
}
