package de.kammerchorwernigerode.app.participate.event.presentation;

import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

@Component
public class EventPeriodDateTimePrinter extends EventPeriodPrinter {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter
        .ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT);

    @Override
    protected DateTimeFormatter getFormatter() {
        return FORMATTER;
    }
}
