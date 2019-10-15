package de.vinado.wicket.participate.model;

import lombok.var;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Date;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Vincent Nadoll
 */
public class MockedEvent {

    private static final Date NOW = new Date();
    private static final String DEFAULT_LOCATION = "Wernigerode";

    public static Event mockEvent(Long id) {
        return mockEvent(id, NOW);
    }

    public static Event mockEvent(Long id, Date startDate) {
        return mockEvent(id, startDate, DEFAULT_LOCATION);
    }

    public static Event mockEvent(Long id, Date startDate, String location) {
        var event = mock(Event.class);
        when(event.getId()).thenReturn(id);
        when(event.getStartDate()).thenReturn(startDate);
        when(event.getEndDate()).thenReturn(DateUtils.addDays(startDate, 3));
        when(event.getLocation()).thenReturn(location);
        return event;
    }
}
