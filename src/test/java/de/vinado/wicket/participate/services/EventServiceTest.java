package de.vinado.wicket.participate.services;

import de.vinado.wicket.participate.configuration.ApplicationProperties;
import de.vinado.wicket.participate.email.service.EmailService;
import de.vinado.wicket.participate.model.Participant;
import lombok.var;
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

    private PersonService personService;
    private EmailService emailService;
    private ApplicationProperties applicationProperties;
    private EventServiceImpl service;

    @Before
    public void setUp() throws Exception {
        personService = mock(PersonService.class);
        emailService = mock(EmailService.class);
        applicationProperties = mock(ApplicationProperties.class);

        service = new EventServiceImpl(personService, emailService, applicationProperties);

        doReturn(13).when(applicationProperties).getDeadlineOffset();
    }

    @Test
    public void testAfterDeadline_after() {
        var event = mockEvent(1L, DateUtils.addDays(NOW, 7));
        var participant = mock(Participant.class);
        when(participant.getEvent()).thenReturn(event);

        var after = service.afterDeadline(participant);

        assertTrue(after);
    }

    @Test
    public void testAfterDeadline_before() {
        var event = mockEvent(1L, DateUtils.addDays(NOW, 30));
        var participant = mock(Participant.class);
        when(participant.getEvent()).thenReturn(event);

        var after = service.afterDeadline(participant);

        assertFalse(after);
    }
}
