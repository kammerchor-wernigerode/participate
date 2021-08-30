package de.vinado.wicket.participate.services;

import de.vinado.wicket.participate.configuration.ApplicationProperties;
import de.vinado.wicket.participate.email.EmailBuilderFactory;
import de.vinado.wicket.participate.email.PreconfiguredEmailBuilderFactory;
import de.vinado.wicket.participate.email.service.EmailService;
import de.vinado.wicket.participate.model.Event;
import de.vinado.wicket.participate.model.Participant;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static de.vinado.wicket.participate.model.MockedEvent.mockEvent;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Vincent Nadoll
 */
public class EventServiceTest {

    private static final Date NOW = new Date();

    private EventServiceImpl service;

    @Before
    public void setUp() {
        PersonService personService = mock(PersonService.class);
        EmailService emailService = mock(EmailService.class);
        ApplicationProperties applicationProperties = mock(ApplicationProperties.class);
        EmailBuilderFactory emailBuilderFactory = new PreconfiguredEmailBuilderFactory(applicationProperties);

        service = new EventServiceImpl(personService, emailService, applicationProperties, emailBuilderFactory);

        doReturn(13).when(applicationProperties).getDeadlineOffset();
    }

    @Test
    public void testDeadlineAfter() {
        Event event = mockEvent(1L, DateUtils.addDays(NOW, 7));
        Participant participant = mock(Participant.class);
        when(participant.getEvent()).thenReturn(event);

        boolean after = service.hasDeadlineExpired(participant);

        assertTrue(after);
    }

    @Test
    public void testDeadlineBefore() {
        Event event = mockEvent(1L, DateUtils.addDays(NOW, 30));
        Participant participant = mock(Participant.class);
        when(participant.getEvent()).thenReturn(event);

        boolean after = service.hasDeadlineExpired(participant);

        assertFalse(after);
    }
}
