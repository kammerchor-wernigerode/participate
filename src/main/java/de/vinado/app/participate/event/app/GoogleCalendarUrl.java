package de.vinado.app.participate.event.app;

import de.vinado.wicket.participate.model.Event;
import lombok.NonNull;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.ZoneId;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.Optional;

public class GoogleCalendarUrl implements CalendarUrl {

    public static final String BASE_URL = "https://calendar.google.com/calendar/embed";

    @Override
    public URI apply(@NonNull Event event, @NonNull Locale locale) {
        return UriComponentsBuilder.fromHttpUrl(BASE_URL)
            .queryParam("src", source())
            .queryParam("wkst", weekStart(locale))
            .queryParam("ctz", timezone())
            .queryParam("dates", dates(event, locale))
            .build(true).toUri();
    }

    private URI source() {
        return URI.create("");
    }

    private int weekStart(Locale locale) {
        DayOfWeek firstDayOfWeek = WeekFields.of(locale).getFirstDayOfWeek();
        int dayOfWeek = firstDayOfWeek.getValue() % 7;
        return dayOfWeek + 1;
    }

    private ZoneId timezone() {
        return ZoneId.systemDefault();
    }

    private String dates(Event event, Locale locale) {
        DateFormat formatter = new SimpleDateFormat("yyyyMMdd", locale);
        String start = startDate(event, formatter);
        String end = endDate(event, formatter);
        return start + "/" + end;
    }

    private String startDate(Event event, DateFormat formatter) {
        return formatter.format(event.getStartDate());
    }

    private String endDate(Event event, DateFormat formatter) {
        return Optional.ofNullable(event.getEndDate())
            .map(formatter::format)
            .orElseGet(() -> startDate(event, formatter));
    }
}
