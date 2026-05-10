package de.kammerchorwernigerode.app.participate.event.presentation;

import de.kammerchorwernigerode.app.participate.event.presentation.model.EventProjection;
import org.apache.wicket.util.string.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.format.Printer;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

import lombok.Setter;

import static java.util.function.Predicate.not;

@Component
public class EventTitlePrinter implements Printer<EventProjection> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MMMM yyyy");

    @Setter(onMethod_ = {@Autowired})
    private MessageSource messageSource;

    @Override
    public String print(EventProjection event, Locale locale) {
        DateTimeFormatter formatter = FORMATTER
            .localizedBy(locale)
            .withZone(event.getStartZoneId());

        String monthYear = formatter.format(event.getStartDateTime());
        String name = Optional.ofNullable(event.getSummary())
            .filter(not(Strings::isEmpty))
            .orElseGet(() -> messageSource.getMessage("event", null, locale));

        return name + " " + monthYear;
    }
}
