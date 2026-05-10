package de.kammerchorwernigerode.app.participate.event.presentation;

import de.kammerchorwernigerode.app.participate.event.presentation.model.AttendeeProjection;
import org.springframework.format.Printer;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

@Component
public class AttendeePeriodPrinter implements Printer<AttendeeProjection> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);

    @Override
    public String print(AttendeeProjection attendee, Locale locale) {
        DateTimeFormatter formatter = FORMATTER.localizedBy(locale);

        LocalDateTime fromDateTime = attendee.getFromDateTime();
        LocalDateTime toDateTime = attendee.getToDateTime();
        return formatter.format(fromDateTime) + "–" + formatter.format(toDateTime);
    }
}
