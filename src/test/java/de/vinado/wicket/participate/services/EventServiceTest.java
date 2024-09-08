package de.vinado.wicket.participate.services;

import de.vinado.app.participate.event.model.EventName;
import de.vinado.app.participate.notification.email.app.EmailService;
import de.vinado.app.participate.notification.email.model.TemplatedEmailFactory;
import de.vinado.wicket.participate.configuration.ApplicationProperties;
import de.vinado.wicket.participate.model.Event;
import de.vinado.wicket.participate.model.Participant;
import freemarker.template.Configuration;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.format.Printer;

import java.net.URI;
import java.util.Date;
import java.util.UUID;

import static de.vinado.wicket.participate.model.MockedEvent.mockEvent;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EventServiceTest {

    private static final Date NOW = new Date();

    private EventServiceImpl service;

    @BeforeEach
    void setUp() {
        PersonService personService = mock(PersonService.class);
        EmailService emailService = mock(EmailService.class);
        Configuration configuration = mock(Configuration.class);
        ApplicationProperties applicationProperties = mock(ApplicationProperties.class);
        TemplatedEmailFactory emailFactory = new TemplatedEmailFactory(configuration, applicationProperties);
        Printer<EventName> eventNamePrinter = mock(Printer.class);

        service = new EventServiceImpl(personService, emailService, applicationProperties, emailFactory, (event, locale) -> randomUri(), eventNamePrinter);

        doReturn(13).when(applicationProperties).getDeadlineOffset();
    }

    private static URI randomUri() {
        UUID uuid = UUID.randomUUID();
        return URI.create(uuid.toString());
    }

    @Test
    void testDeadlineAfter() {
        Event event = mockEvent(1L, DateUtils.addDays(NOW, 7));
        Participant participant = mock(Participant.class);
        when(participant.getEvent()).thenReturn(event);

        boolean after = service.hasDeadlineExpired(participant);

        assertTrue(after);
    }

    @Test
    void testDeadlineBefore() {
        Event event = mockEvent(1L, DateUtils.addDays(NOW, 30));
        Participant participant = mock(Participant.class);
        when(participant.getEvent()).thenReturn(event);

        boolean after = service.hasDeadlineExpired(participant);

        assertFalse(after);
    }
}
