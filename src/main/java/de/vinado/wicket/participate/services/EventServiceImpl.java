package de.vinado.wicket.participate.services;

import de.vinado.wicket.participate.common.ParticipateUtils;
import de.vinado.wicket.participate.configuration.ApplicationProperties;
import de.vinado.wicket.participate.email.Email;
import de.vinado.wicket.participate.email.EmailAttachment;
import de.vinado.wicket.participate.email.EmailBuilderFactory;
import de.vinado.wicket.participate.email.service.EmailService;
import de.vinado.wicket.participate.model.Event;
import de.vinado.wicket.participate.model.EventDetails;
import de.vinado.wicket.participate.model.InvitationStatus;
import de.vinado.wicket.participate.model.Participant;
import de.vinado.wicket.participate.model.Person;
import de.vinado.wicket.participate.model.Singer;
import de.vinado.wicket.participate.model.Terminable;
import de.vinado.wicket.participate.model.User;
import de.vinado.wicket.participate.model.dtos.EventDTO;
import de.vinado.wicket.participate.model.dtos.ParticipantDTO;
import de.vinado.wicket.participate.model.ical4j.SimpleDateProperty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.parameter.Cn;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Created;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.Location;
import net.fortuna.ical4j.model.property.Method;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.model.property.Version;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.wicket.util.string.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static de.vinado.wicket.participate.common.DateUtils.toLocalDate;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.temporal.ChronoUnit.DAYS;

/**
 * Provides interaction with the database. The service implementation takes care of {@link Event} and Event related
 * objects.
 *
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
@Primary
@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl extends DataService implements EventService {

    private final PersonService personService;
    private final EmailService emailService;
    private final ApplicationProperties applicationProperties;
    private final EmailBuilderFactory emailBuilderFactory;

    @Value("${spring.application.name:KCH Paritcipate}")
    private String applicationName;

    @Override
    @PersistenceContext
    public void setEntityManager(final EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Event createEvent(final EventDTO dto) {
        if (Strings.isEmpty(dto.getName())) {
            dto.setName(ParticipateUtils.getGenericEventName(dto));
        }

        // Event
        Event event = new Event(
            dto.getName(),
            dto.getEventType(),
            dto.getLocation(),
            dto.getDescription(),
            dto.getStartDate(),
            dto.getEndDate()
        );
        event = save(event);

        // Event to singer
        for (Singer singer : personService.getSingers()) {
            createParticipant(event, singer);
        }

        return event;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Event saveEvent(final EventDTO dto) {
        final Event loadedEvent = load(Event.class, dto.getEvent().getId());

        if (Strings.isEmpty(dto.getName())) {
            dto.setName(ParticipateUtils.getGenericEventName(dto));
        }

        loadedEvent.setName(dto.getName());
        loadedEvent.setEventType(dto.getEventType());
        loadedEvent.setLocation(dto.getLocation());
        loadedEvent.setDescription(dto.getDescription());
        loadedEvent.setStartDate(dto.getStartDate());
        loadedEvent.setEndDate(dto.getEndDate());

        // TODO Compare old and new and send notification if necessary

        return save(loadedEvent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeEvent(final Event event) {
        final Event loadedEvent = load(Event.class, event.getId());
        loadedEvent.setActive(false);
        save(loadedEvent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Participant createParticipant(final Event event, final Singer singer) {
        final Participant participant = new Participant(event, singer, generateEventToken(20), InvitationStatus.UNINVITED);
        return save(participant);
    }

    /**
     * Generates a new unique event token, by recursively checking for existing tokens.
     *
     * @param length the length of the token to generate
     * @return an unique token
     */
    private String generateEventToken(final int length) {
        final String accessToken = RandomStringUtils.randomAlphanumeric(length);

        if (hasToken(accessToken)) {
            return generateEventToken(length);
        } else {
            return accessToken;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Participant saveParticipant(final ParticipantDTO dto) {
        final Participant loadedParticipant = load(Participant.class, dto.getParticipant().getId());
        loadedParticipant.setInvitationStatus(dto.getInvitationStatus());
        loadedParticipant.setFromDate(dto.getFromDate());
        loadedParticipant.setToDate(dto.getToDate());
        loadedParticipant.setCatering(dto.isCatering());
        loadedParticipant.setAccommodation(dto.isAccommodation());
        loadedParticipant.setCarSeatCount(dto.isCar() ? dto.getCarSeatCount() : -1);
        loadedParticipant.setComment(dto.getComment());
        return save(loadedParticipant);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Participant acceptEvent(final ParticipantDTO dto) {
        dto.setInvitationStatus(InvitationStatus.ACCEPTED);
        return saveParticipant(dto);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Participant declineEvent(final ParticipantDTO dto) {
        dto.setInvitationStatus(InvitationStatus.DECLINED);
        dto.setFromDate(null);
        dto.setToDate(null);
        dto.setCatering(false);
        dto.setAccommodation(false);
        dto.setCar(false);
        dto.setCarSeatCount((short) 0);
        return saveParticipant(dto);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasToken(final String token) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        final Root<Participant> root = criteriaQuery.from(Participant.class);
        criteriaQuery.select(criteriaBuilder.count(root));
        criteriaQuery.where(criteriaBuilder.equal(root.get("token"), token));
        return 0 != entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasUpcomingEvents() {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        final Root<Event> root = criteriaQuery.from(Event.class);
        criteriaQuery.select(criteriaBuilder.count(root));
        criteriaQuery.where(forActive(criteriaBuilder, root), forUpcomingDate(criteriaBuilder, root));
        return 0 != entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Participant getLatestParticipant(final Singer singer) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Participant> criteriaQuery = criteriaBuilder.createQuery(Participant.class);
        final Root<Participant> root = criteriaQuery.from(Participant.class);
        final Join<Participant, Event> eventJoin = root.join("event");
        final Predicate forSinger = criteriaBuilder.equal(root.get("singer"), singer);
        criteriaQuery.where(forSinger, forUpcomingDate(criteriaBuilder, eventJoin), forActive(criteriaBuilder, eventJoin));
        criteriaQuery.orderBy(criteriaBuilder.asc(eventJoin.get("startDate")));
        try {
            return entityManager.createQuery(criteriaQuery).setMaxResults(1).getSingleResult();
        } catch (NoResultException e) {
            log.trace("Could not find Participant of latest event for Singer /w id={}", singer.getId());
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EventDetails getLatestEventDetails() {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<EventDetails> criteriaQuery = criteriaBuilder.createQuery(EventDetails.class);
        final Root<EventDetails> root = criteriaQuery.from(EventDetails.class);
        criteriaQuery.where(forUpcomingDate(criteriaBuilder, root), forActive(criteriaBuilder, root));
        try {
            return entityManager.createQuery(criteriaQuery).setMaxResults(1).getSingleResult();
        } catch (NoResultException e) {
            log.trace("Could not find any upcoming Event");
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Event getLatestEvent() {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Event> criteriaQuery = criteriaBuilder.createQuery(Event.class);
        final Root<Event> root = criteriaQuery.from(Event.class);
        criteriaQuery.where(forUpcomingDate(criteriaBuilder, root), forActive(criteriaBuilder, root));
        criteriaQuery.orderBy(criteriaBuilder.asc(root.get("startDate")));
        try {
            return entityManager.createQuery(criteriaQuery).setMaxResults(1).getSingleResult();
        } catch (NoResultException e) {
            log.trace("Could not find any upcoming Event");
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EventDetails getSuccessor(final EventDetails eventDetails) {
        final Date startDate = load(EventDetails.class, eventDetails.getId()).getStartDate();

        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<EventDetails> criteriaQuery = criteriaBuilder.createQuery(EventDetails.class);
        final Root<EventDetails> root = criteriaQuery.from(EventDetails.class);
        final Predicate forStartDate = criteriaBuilder.greaterThan(root.get("startDate"), startDate);
        criteriaQuery.where(forUpcomingDate(criteriaBuilder, root), forStartDate);
        try {
            return entityManager.createQuery(criteriaQuery).setMaxResults(1).getSingleResult();
        } catch (NoResultException e) {
            log.trace("Could not find successor of Event /w id={}", eventDetails.getEvent().getId());
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EventDetails getPredecessor(final EventDetails eventDetails) {
        final Date startDate = load(EventDetails.class, eventDetails.getId()).getStartDate();

        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<EventDetails> criteriaQuery = criteriaBuilder.createQuery(EventDetails.class);
        final Root<EventDetails> root = criteriaQuery.from(EventDetails.class);
        final Predicate forStartDate = criteriaBuilder.lessThan(root.get("startDate"), startDate);
        criteriaQuery.where(forUpcomingDate(criteriaBuilder, root), forStartDate);
        criteriaQuery.orderBy(criteriaBuilder.desc(root.get("startDate")));
        try {
            return entityManager.createQuery(criteriaQuery).setMaxResults(1).getSingleResult();
        } catch (NoResultException e) {
            log.trace("Could not find predecessor of Event /w id={}", eventDetails.getEvent().getId());
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Event> getUpcomingEvents() {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Event> criteriaQuery = criteriaBuilder.createQuery(Event.class);
        final Root<Event> root = criteriaQuery.from(Event.class);
        criteriaQuery.orderBy(criteriaBuilder.asc(root.<Event>get("startDate")));
        criteriaQuery.where(forUpcomingDate(criteriaBuilder, root));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<EventDetails> getUpcomingEventDetails() {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<EventDetails> criteriaQuery = criteriaBuilder.createQuery(EventDetails.class);
        final Root<EventDetails> root = criteriaQuery.from(EventDetails.class);
        final Join<EventDetails, Event> eventJoin = root.join("event");
        criteriaQuery.where(forActive(criteriaBuilder, eventJoin), forUpcomingDate(criteriaBuilder, eventJoin));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getEventTypes() {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<String> criteriaQuery = criteriaBuilder.createQuery(String.class);
        final Root<Event> root = criteriaQuery.from(Event.class);
        criteriaQuery.select(root.get("eventType"));
        criteriaQuery.groupBy(root.<String>get("eventType"));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getLocationList() {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<String> criteriaQuery = criteriaBuilder.createQuery(String.class);
        final Root<Event> root = criteriaQuery.from(Event.class);
        criteriaQuery.select(root.get("location"));
        criteriaQuery.groupBy(root.<String>get("location"));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EventDetails getEventDetails(final Event event) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<EventDetails> criteriaQuery = criteriaBuilder.createQuery(EventDetails.class);
        final Root<EventDetails> root = criteriaQuery.from(EventDetails.class);
        criteriaQuery.where(criteriaBuilder.equal(root.get("event"), event));
        try {
            return entityManager.createQuery(criteriaQuery).getSingleResult();
        } catch (NoResultException e) {
            log.trace("Could not find Event Details for Event /w id={}", event.getId());
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Participant> getParticipants(final Event event) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Participant> criteriaQuery = criteriaBuilder.createQuery(Participant.class);
        final Root<Participant> root = criteriaQuery.from(Participant.class);
        criteriaQuery.where(criteriaBuilder.equal(root.<Event>get("event"), event));
        criteriaQuery.orderBy(criteriaBuilder.asc(root.join("singer").get("lastName")));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Participant> getInvitedParticipants(final Event event) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Participant> criteriaQuery = criteriaBuilder.createQuery(Participant.class);
        final Root<Participant> root = criteriaQuery.from(Participant.class);
        final Predicate forEvent = criteriaBuilder.equal(root.<Event>get("event"), event);
        final Predicate forInvitationStatus = criteriaBuilder.notEqual(root.get("invitationStatus"), InvitationStatus.UNINVITED);
        criteriaQuery.where(forEvent, forInvitationStatus);
        criteriaQuery.orderBy(criteriaBuilder.asc(root.join("singer").get("lastName")));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Participant> getUninvitedParticipants(final Event event) {
        return getParticipants(event, InvitationStatus.UNINVITED);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Participant> getParticipants(final Event event, final InvitationStatus invitationStatus) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Participant> criteriaQuery = criteriaBuilder.createQuery(Participant.class);
        final Root<Participant> root = criteriaQuery.from(Participant.class);
        final Predicate forEvent = criteriaBuilder.equal(root.<Event>get("event"), event);
        final Predicate forInvitationStatus = criteriaBuilder.equal(root.get("invitationStatus"), invitationStatus);
        criteriaQuery.where(forEvent, forInvitationStatus);
        criteriaQuery.orderBy(criteriaBuilder.asc(root.join("singer").get("lastName")));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasParticipant(final Event event) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        final Root<Participant> root = criteriaQuery.from(Participant.class);
        final Predicate forEvent = criteriaBuilder.equal(root.get("event"), event);
        final Predicate forInvited = criteriaBuilder.notEqual(root.get("invitationStatus"), InvitationStatus.UNINVITED);
        criteriaQuery.select(criteriaBuilder.count(root));
        criteriaQuery.where(forInvited, forEvent);
        return 0 != entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Participant getParticipant(final String token) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Participant> criteriaQuery = criteriaBuilder.createQuery(Participant.class);
        final Root<Participant> root = criteriaQuery.from(Participant.class);
        criteriaQuery.where(criteriaBuilder.equal(root.<String>get("token"), token));
        try {
            return entityManager.createQuery(criteriaQuery).getSingleResult();
        } catch (NoResultException e) {
            log.trace("Could not find Participant /w token={}", token);
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Participant> getParticipants(final Singer singer) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Participant> criteriaQuery = criteriaBuilder.createQuery(Participant.class);
        final Root<Participant> root = criteriaQuery.from(Participant.class);
        final Predicate forSinger = criteriaBuilder.equal(root.get("singer"), singer);
        final Join<Participant, Event> eventJoin = root.join("event");
        criteriaQuery.where(forSinger, forActive(criteriaBuilder, eventJoin), forUpcomingDate(criteriaBuilder, eventJoin));
        criteriaQuery.orderBy(
            criteriaBuilder.asc(eventJoin.get("startDate")),
            criteriaBuilder.asc(root.join("singer").get("lastName")));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getToken(final Singer singer, final Event event) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<String> criteriaQuery = criteriaBuilder.createQuery(String.class);
        final Root<Participant> root = criteriaQuery.from(Participant.class);
        final Predicate forSinger = criteriaBuilder.equal(root.get("singer"), singer);
        final Predicate forEvent = criteriaBuilder.equal(root.get("event"), event);
        criteriaQuery.select(root.get("token"));
        criteriaQuery.where(forSinger, forEvent);
        try {
            return entityManager.createQuery(criteriaQuery).getSingleResult();
        } catch (NoResultException e) {
            log.trace("Could not find Participant for Singer /w id={} and Event /w id={}", singer, event);
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int inviteParticipants(final List<Participant> participants, final User organizer) {
        final Stream<Email> invitations = participants
            .stream()
            .map(participant -> {
                final Singer singer = participant.getSinger();
                int offset = 1 - applicationProperties.getDeadlineOffset();
                Date deadline = DateUtils.addDays(participant.getEvent().getStartDate(), offset);

                Email email = emailBuilderFactory.create()
                    .to(singer)
                    .subject(participant.getEvent().getName())
                    .attachments(newEventAttachment(participant.getEvent(), organizer))
                    .data("event", participant.getEvent())
                    .data("singer", singer)
                    .data("acceptLink", ParticipateUtils.generateInvitationLink(applicationProperties.getBaseUrl(), participant.getToken()))
                    .data("deadline", offset > 1 ? null : deadline)
                    .build();

                if (InvitationStatus.UNINVITED.equals(participant.getInvitationStatus())) {
                    final Participant loadedParticipant = load(Participant.class, participant.getId());
                    loadedParticipant.setInvitationStatus(InvitationStatus.PENDING);
                    save(loadedParticipant);
                }

                return email;
            });

        // TODO Create notification template
        emailService.send(invitations, "inviteSinger-txt.ftl", "inviteSinger-html.ftl");
        return participants.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void inviteParticipant(final Participant participant, final User organizer) {
        inviteParticipants(Collections.singletonList(participant), organizer);
    }

    /**
     * Creates a new iCal attachment for event invitations.
     *
     * @param event the {@link Event} with information used in the attachment
     * @return a new {@link EmailAttachment}
     */
    private EmailAttachment newEventAttachment(final Event event, final User organizer) {
        final String eventName = event.getEventType() + " in " + event.getLocation();

        final net.fortuna.ical4j.model.Calendar cal = new net.fortuna.ical4j.model.Calendar();
        cal.getProperties().add(new ProdId(String.format(
            "-//%s//%s %s//DE",
            applicationProperties.getCustomer(),
            applicationName,
            applicationProperties.getVersion()
        )));
        cal.getProperties().add(Version.VERSION_2_0);
        cal.getProperties().add(CalScale.GREGORIAN);
        cal.getProperties().add(Method.REQUEST);

        final UUID uuid = UUID.randomUUID();
        final VEvent vEvent = new VEvent();
        vEvent.getProperties().add(new Uid(uuid.toString()));
        vEvent.getProperties().add(new Summary(eventName));
        vEvent.getProperties().add(new Description(event.getDescription()));
        vEvent.getProperties().add(new Location(event.getLocation()));
        vEvent.getProperties().add(new Created(new DateTime(event.getCreationDate())));
        vEvent.getProperties().add(vEvent.getDateStamp());


        vEvent.getProperties().add(new SimpleDateProperty(SimpleDateProperty.Type.DTSTART, event.getStartDate()));
        if (!event.getStartDate().equals(event.getEndDate())) {
            vEvent.getProperties().add(new SimpleDateProperty(SimpleDateProperty.Type.DTEND, event.getEndDate()));
        }

        String orgaEmail = Optional.ofNullable(organizer)
            .map(User::getPerson)
            .map(Person::getEmail)
            .orElse(applicationProperties.getMail().getSender());
        ParameterList organizerParameters = new ParameterList();
        organizerParameters.add(new Cn(applicationProperties.getCustomer()));
        Organizer vOrganizer = new Organizer(organizerParameters, URI.create("mailto:" + orgaEmail));
        vEvent.getProperties().add(vOrganizer);

        cal.getComponents().add(vEvent);

        try {
            return new EmailAttachment("participate-event.ics", MediaType.valueOf("text/calendar"), cal.toString().getBytes(UTF_8.name()));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UnsupportedEncodingException", e);
        }
    }

    @Override
    public boolean hasDeadlineExpired(Participant participant) {
        if (0 > applicationProperties.getDeadlineOffset()) {
            return false;
        }

        LocalDate now = LocalDate.now();
        LocalDate startDate = toLocalDate(participant.getEvent().getStartDate());
        return applicationProperties.getDeadlineOffset() >= DAYS.between(now, startDate);
    }

    @Override
    public Stream<EventDetails> listAll() {
        return getAll(EventDetails.class).stream();
    }

    private static Predicate forUpcomingDate(final CriteriaBuilder criteriaBuilder, final Path<? extends Terminable> eventPath) {
        return criteriaBuilder.greaterThanOrEqualTo(eventPath.get("endDate"), new Date());
    }
}
