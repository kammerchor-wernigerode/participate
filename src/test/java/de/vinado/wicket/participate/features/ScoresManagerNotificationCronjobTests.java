package de.vinado.wicket.participate.features;

import de.vinado.wicket.participate.email.Email;
import de.vinado.wicket.participate.email.EmailBuilderFactory;
import de.vinado.wicket.participate.email.service.EmailService;
import de.vinado.wicket.participate.model.Event;
import de.vinado.wicket.participate.model.Participant;
import de.vinado.wicket.participate.model.Singer;
import de.vinado.wicket.participate.services.EventService;
import de.vinado.wicket.participate.services.PersonService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScoresManagerNotificationCronjobTests {

    private static LocalDate now;

    private @Mock EmailBuilderFactory emailBuilderFactory;
    private @Mock ScoresManagerNotificationCronjob.Configuration configuration;
    private @Mock EventService eventService;
    private @Mock PersonService personService;
    private @Mock EmailService emailService;

    private ScoresManagerNotificationCronjob cronjob;

    @BeforeAll
    static void beforeAll() {
        now = LocalDate.now();
    }

    @BeforeEach
    void setUp() {
        cronjob = new ScoresManagerNotificationCronjob(emailBuilderFactory,
            configuration,
            eventService,
            personService,
            emailService);
    }

    @Test
    void creatingNullArguments_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> new ScoresManagerNotificationCronjob(null, configuration, eventService, personService, emailService));
        assertThrows(IllegalArgumentException.class, () -> new ScoresManagerNotificationCronjob(emailBuilderFactory, null, eventService, personService, emailService));
        assertThrows(IllegalArgumentException.class, () -> new ScoresManagerNotificationCronjob(emailBuilderFactory, configuration, null, personService, emailService));
        assertThrows(IllegalArgumentException.class, () -> new ScoresManagerNotificationCronjob(emailBuilderFactory, configuration, eventService, null, emailService));
        assertThrows(IllegalArgumentException.class, () -> new ScoresManagerNotificationCronjob(emailBuilderFactory, configuration, eventService, personService, null));
    }

    @Test
    void nullScoresManagerEmail_shouldThrowException() {
        when(configuration.getScoresManagerEmail()).thenReturn(null);

        assertThrows(IllegalArgumentException.class, cronjob::run);
    }

    @Test
    void emptyEventList_shouldNotSendEmail() {
        String emailAddress = "jane.doe@example.com";
        when(configuration.getScoresManagerEmail()).thenReturn(emailAddress);
        when(personService.getPerson(eq(emailAddress))).thenReturn(null);
        when(eventService.getUpcomingEvents()).thenReturn(Collections.emptyList());

        cronjob.run();

        verify(emailService).send(argThat(isEmpty()), any(String.class), isNull());
    }

    @Test
    void emptyParticipantList_shouldSendEmail() {
        String emailAddress = "jane.doe@example.com";
        Date startDate = date(now.plusDays(7));
        Event event = createEvent(startDate);
        when(configuration.getScoresManagerEmail()).thenReturn(emailAddress);
        when(personService.getPerson(eq(emailAddress))).thenReturn(null);
        when(eventService.getUpcomingEvents()).thenReturn(List.of(event));
        when(configuration.getOffset()).thenReturn(Integer.MAX_VALUE);
        when(eventService.getInvitedParticipants(eq(event))).thenReturn(Collections.emptyList());
        when(emailBuilderFactory.create()).thenAnswer(i -> Email.builder(emailAddress));

        cronjob.run();

        verify(emailService).send(argThat(isNotEmpty()), any(String.class), isNull());
    }

    @Test
    void bygoneEvent_shouldNotSendEmail() {
        String emailAddress = "jane.doe@example.com";
        Date startDate = date(now.minusDays(7));
        Event event = mock(Event.class);
        when(event.getStartDate()).thenReturn(startDate);
        when(configuration.getScoresManagerEmail()).thenReturn(emailAddress);
        when(personService.getPerson(eq(emailAddress))).thenReturn(null);
        when(eventService.getUpcomingEvents()).thenReturn(List.of(event));
        when(configuration.getOffset()).thenReturn(Integer.MAX_VALUE);

        cronjob.run();

        verify(emailService).send(argThat(isEmpty()), any(String.class), isNull());
    }

    @Test
    void outOfOffset_shouldNotSendEmail() {
        String emailAddress = "jane.doe@example.com";
        Date startDate = date(now.plusDays(7));
        Event event = mock(Event.class);
        when(event.getStartDate()).thenReturn(startDate);
        when(configuration.getScoresManagerEmail()).thenReturn(emailAddress);
        when(personService.getPerson(eq(emailAddress))).thenReturn(null);
        when(eventService.getUpcomingEvents()).thenReturn(List.of(event));
        when(configuration.getOffset()).thenReturn(3);

        cronjob.run();

        verify(emailService).send(argThat(isEmpty()), any(String.class), isNull());
    }

    @Test
    void withinScopeAndOffset_shouldSendEmail() {
        String emailAddress = "jane.doe@example.com";
        Date startDate = date(now.plusDays(7));
        boolean accepted = true;
        Event event = createEvent(startDate);
        Participant participant = createParticipant(accepted);
        when(configuration.getScoresManagerEmail()).thenReturn(emailAddress);
        when(personService.getPerson(eq(emailAddress))).thenReturn(null);
        when(eventService.getUpcomingEvents()).thenReturn(List.of(event));
        when(configuration.getOffset()).thenReturn(Integer.MAX_VALUE);
        when(eventService.getInvitedParticipants(eq(event))).thenReturn(List.of(participant));
        when(emailBuilderFactory.create()).thenAnswer(i -> Email.builder(emailAddress));

        cronjob.run();

        verify(emailService).send(argThat(isNotEmpty()), any(String.class), isNull());
    }

    @Test
    void notAcceptedParticipation_shouldSendEmailAnyways() {
        String emailAddress = "jane.doe@example.com";
        Date startDate = date(now.plusDays(7));
        boolean accepted = false;
        Event event = createEvent(startDate);
        Participant participant = createParticipant(accepted);
        when(configuration.getScoresManagerEmail()).thenReturn(emailAddress);
        when(personService.getPerson(eq(emailAddress))).thenReturn(null);
        when(eventService.getUpcomingEvents()).thenReturn(List.of(event));
        when(configuration.getOffset()).thenReturn(Integer.MAX_VALUE);
        when(eventService.getInvitedParticipants(eq(event))).thenReturn(List.of(participant));
        when(emailBuilderFactory.create()).thenAnswer(i -> Email.builder(emailAddress));

        cronjob.run();

        verify(emailService).send(argThat(isNotEmpty()), any(String.class), isNull());
    }

    @Test
    void multipleEvents_shouldSendMultipleEmails() {
        String emailAddress = "jane.doe@example.com";
        Date startDate = date(now.plusDays(7));
        Event event_0 = createEvent(startDate);
        Event event_1 = createEvent(startDate);
        when(configuration.getScoresManagerEmail()).thenReturn(emailAddress);
        when(personService.getPerson(eq(emailAddress))).thenReturn(null);
        when(eventService.getUpcomingEvents()).thenReturn(List.of(event_0, event_1));
        when(configuration.getOffset()).thenReturn(Integer.MAX_VALUE);
        when(eventService.getInvitedParticipants(eq(event_0))).thenReturn(Collections.emptyList());
        when(eventService.getInvitedParticipants(eq(event_1))).thenReturn(Collections.emptyList());
        when(emailBuilderFactory.create()).thenAnswer(i -> Email.builder(emailAddress));

        cronjob.run();

        verify(emailService).send(argThat(containsElements(2)), any(String.class), isNull());
    }

    @Test
    @SneakyThrows
    void creatingCsv_shouldWriteSingers() {
        Singer singer_0 = mock(Singer.class);
        when(singer_0.getLastName()).thenReturn("Doe");
        when(singer_0.getFirstName()).thenReturn("Jane");
        Singer singer_1 = mock(Singer.class);
        when(singer_1.getLastName()).thenReturn("Doe");
        when(singer_1.getFirstName()).thenReturn("John");

        byte[] csv = cronjob.getAttendeeByteArray(Arrays.asList(singer_0, singer_1));

        assertEquals("Doe, Jane\nDoe, John\n", new String(csv));
    }

    private static Event createEvent(Date startDate) {
        Event event = mock(Event.class);
        when(event.getStartDate()).thenReturn(startDate);
        when(event.getName()).thenReturn("Rehearsal Weekend");
        return event;
    }

    private static Participant createParticipant(boolean accepted) {
        Participant participant = mock(Participant.class, RETURNS_DEEP_STUBS);
        lenient().when(participant.getSinger().getFirstName()).thenReturn("John");
        lenient().when(participant.getSinger().getLastName()).thenReturn("Doe");
        when(participant.isAccepted()).thenReturn(accepted);
        return participant;
    }

    private static ArgumentMatcher<Collection<Email>> isEmpty() {
        return Collection::isEmpty;
    }

    private static ArgumentMatcher<Collection<Email>> isNotEmpty() {
        return argument -> !argument.isEmpty();
    }

    private ArgumentMatcher<Collection<Email>> containsElements(int i) {
        return argument -> argument.size() == i;
    }

    private static Date date(LocalDate date) {
        ZonedDateTime dateTime = date.atStartOfDay(ZoneId.systemDefault());
        return Date.from(dateTime.toInstant());
    }
}
