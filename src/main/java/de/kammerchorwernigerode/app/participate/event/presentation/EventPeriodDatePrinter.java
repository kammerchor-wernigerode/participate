package de.kammerchorwernigerode.app.participate.event.presentation;

import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

@Component
public class EventPeriodDatePrinter extends EventPeriodPrinter {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter
        .ofLocalizedDate(FormatStyle.SHORT);

    @Override
    protected DateTimeFormatter getFormatter() {
        return FORMATTER;
    }
}
