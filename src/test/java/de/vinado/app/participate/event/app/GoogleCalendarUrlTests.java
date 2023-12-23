package de.vinado.app.participate.event.app;

import de.vinado.wicket.participate.model.Event;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GoogleCalendarUrlTests {

    private static final String SUNDAY = "1";
    private static final String MONDAY = "2";

    private CalendarUrl calendarUrl;

    @BeforeEach
    void setUp() {
        calendarUrl = createCalendarUrl(Random.uri(), Arbitrary.zoneId());
    }

    @ParameterizedTest
    @MethodSource("weekStarts")
    void applyLocale_shouldSetStartOfWeek(Locale locale, String wkst) {
        Event event = createEvent(Arbitrary.date(), Arbitrary.date());

        URI uri = calendarUrl.apply(event, locale);

        assertEquals(wkst, queryParam("wkst", uri));
    }

    private static Arguments[] weekStarts() {
        return new Arguments[]{Arguments.of(Locale.US, SUNDAY), Arguments.of(Locale.GERMANY, MONDAY),};
    }

    @Test
    void applyDates_shouldSetDates() {
        Date start = new Date(2024 - 1900, Calendar.MAY, 3);
        Date end = new Date(2024 - 1900, Calendar.MAY, 5);
        Event event = createEvent(start, end);

        URI uri = calendarUrl.apply(event, Arbitrary.locale());

        assertEquals("20240503/20240505", queryParam("dates", uri));
    }

    @Test
    void applyNullEndDate_shouldReuseStartDate() {
        Date start = new Date(2024 - 1900, Calendar.MAY, 3);
        Date end = null;
        Event event = createEvent(start, end);

        URI uri = calendarUrl.apply(event, Arbitrary.locale());

        assertEquals("20240503/20240503", queryParam("dates", uri));
    }

    @Test
    void applyTimezone_shouldSetTimezone() {
        ZoneId timezone = ZoneId.of("Europe/Berlin");
        CalendarUrl calendarUrl = createCalendarUrl(Random.uri(), timezone);
        Event event = createEvent(Arbitrary.date(), Arbitrary.date());

        URI uri = calendarUrl.apply(event, Arbitrary.locale());

        assertEquals("Europe/Berlin", queryParam("ctz", uri));
    }

    @Test
    void applyNullTimezone_shouldSetSystemDefaultTimezone() {
        CalendarUrl calendarUrl = createCalendarUrl(Random.uri(), null);
        Event event = createEvent(Arbitrary.date(), Arbitrary.date());

        URI uri = calendarUrl.apply(event, Arbitrary.locale());

        assertEquals(ZoneId.systemDefault().toString(), queryParam("ctz", uri));
    }

    @Test
    void apply_shouldSetSource() {
        CalendarUrl calendarUrl = createCalendarUrl(URI.create("foobar"), Arbitrary.zoneId());
        Event event = createEvent(Arbitrary.date(), Arbitrary.date());

        URI uri = calendarUrl.apply(event, Arbitrary.locale());

        assertEquals("foobar", queryParam("src", uri));
    }

    private static CalendarUrl createCalendarUrl(URI uri, ZoneId timezone) {
        GoogleCalendarUrlProperties properties = new GoogleCalendarUrlProperties(uri, timezone);
        return new GoogleCalendarUrl(properties);
    }

    private static Event createEvent(Date start, Date end) {
        Event event = mock(Event.class);
        when(event.getStartDate()).thenReturn(start);
        when(event.getEndDate()).thenReturn(end);
        return event;
    }

    private static String queryParam(String key, URI uri) {
        return splitQuery(uri).get(key);
    }

    public static Map<String, String> splitQuery(URI uri) {
        Map<String, String> parameters = new LinkedHashMap<>();
        String query = uri.getQuery();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            String key = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8);
            String value = URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8);
            parameters.put(key, value);
        }
        return parameters;
    }


    private static final class Arbitrary {

        public static Date date() {
            return new Date();
        }

        public static Locale locale() {
            return Locale.getDefault();
        }

        public static ZoneId zoneId() {
            return ZoneId.systemDefault();
        }
    }

    private static final class Random {

        public static URI uri() {
            return URI.create(UUID.randomUUID().toString());
        }
    }
}
