package de.vinado.wicket.participate.services;

import de.vinado.wicket.participate.ParticipateApplication;
import de.vinado.wicket.participate.ParticipateSession;
import de.vinado.wicket.participate.common.ParticipateUtils;
import de.vinado.wicket.participate.configuration.ApplicationProperties;
import de.vinado.wicket.participate.data.Event;
import de.vinado.wicket.participate.data.EventDetails;
import de.vinado.wicket.participate.data.InvitationStatus;
import de.vinado.wicket.participate.data.Participant;
import de.vinado.wicket.participate.data.Person;
import de.vinado.wicket.participate.data.Singer;
import de.vinado.wicket.participate.data.Voice;
import de.vinado.wicket.participate.data.dtos.EventDTO;
import de.vinado.wicket.participate.data.dtos.ParticipantDTO;
import de.vinado.wicket.participate.data.email.EmailAttachment;
import de.vinado.wicket.participate.data.email.MailData;
import de.vinado.wicket.participate.data.filters.DetailedParticipantFilter;
import de.vinado.wicket.participate.data.filters.EventFilter;
import de.vinado.wicket.participate.data.filters.ParticipantFilter;
import de.vinado.wicket.participate.data.ical4j.SimpleDateProperty;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Property;
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
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.util.string.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.internet.AddressException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Provides interaction with the database. The service takes care of {@link Event} and Event related objects.
 *
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
@Service
public class EventService extends DataService {

    private final static Logger LOGGER = LoggerFactory.getLogger(EventService.class);

    @Autowired
    private PersonService personService;

    @Autowired
    private EmailService emailService;

    /**
     * {@link de.vinado.wicket.participate.services.DataService}
     *
     * @param entityManager Entity manager
     */
    @PersistenceContext
    public void setEntityManager(final EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Creates an {@link de.vinado.wicket.participate.data.Event}
     *
     * @param dto {@link EventDTO}
     * @return {@link de.vinado.wicket.participate.data.Event}
     */
    @Transactional
    public Event createEvent(final EventDTO dto) {
        dto.setIdentifier((new SimpleDateFormat("yyyyMM").format(dto.getStartDate())
            + dto.getEventType()
            + dto.getLocation()).replaceAll("[^A-Za-z0-9]", "").toUpperCase());

        if (Strings.isEmpty(dto.getName())) {
            dto.setName(ParticipateUtils.getGenericEventName(dto));
        }

        // Event
        Event event = new Event(
            dto.getIdentifier(),
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
     * Saves an existing {@link de.vinado.wicket.participate.data.Event}
     *
     * @param dto {@link EventDTO}
     * @return {@link de.vinado.wicket.participate.data.Event}
     */
    @Transactional
    public Event saveEvent(final EventDTO dto) {
        final Event loadedEvent = load(Event.class, dto.getEvent().getId());
        final Event clonedEvent = loadedEvent;

        if (Strings.isEmpty(dto.getName())) {
            dto.setName(ParticipateUtils.getGenericEventName(dto));
        }

        loadedEvent.setName(dto.getName());
        loadedEvent.setEventType(dto.getEventType());
        loadedEvent.setLocation(dto.getLocation());
        loadedEvent.setDescription(dto.getDescription());
        loadedEvent.setStartDate(dto.getStartDate());
        loadedEvent.setEndDate(dto.getEndDate());

        if (!clonedEvent.equals(loadedEvent)) {
            if (!ParticipateApplication.get().isInDevelopmentMode()) {
                // TODO Write notification template
                //inviteParticipants(loadedEvent, getParticipants(loadedEvent));
            } else {
                inviteParticipants(loadedEvent, Collections.singletonList(getParticipant(
                    ParticipateSession.get().getUser().getPerson().getEmail(), loadedEvent.getId())), false);
            }
        }

        return save(loadedEvent);
    }


    /**
     * Actually removes the {@link Event} and the {@link Participant} from the database.
     *
     * @param event {@link Event}
     */
    @Transactional
    public void removeEvent(final Event event) {
        final Event loadedEvent = load(Event.class, event.getId());
        loadedEvent.setActive(false);
        save(loadedEvent);
    }

    /**
     * Creates the mapping between {@link Event} and {@link Singer}
     *
     * @param event  Event
     * @param singer Singer
     * @return Created mapping
     */
    @Transactional
    public Participant createParticipant(final Event event, final Singer singer) {
        final Participant participant = new Participant(event, singer, generateEventToken(20), InvitationStatus.UNINVITED);
        return save(participant);
    }

    private String generateEventToken(final int length) {
        final String accessToken = RandomStringUtils.randomAlphanumeric(length);

        if (hasEventToken(accessToken)) {
            return generateEventToken(length);
        } else {
            return accessToken;
        }
    }

    @Transactional
    public Participant saveParticipant(final ParticipantDTO dto) {
        final Participant loadedParticipant = load(Participant.class, dto.getParticipant().getId());
        loadedParticipant.setInvitationStatus(dto.getInvitationStatus());
        loadedParticipant.setFromDate(dto.getFromDate());
        loadedParticipant.setToDate(dto.getToDate());
        loadedParticipant.setCatering(dto.isCatering());
        loadedParticipant.setAccommodation(dto.isAccommodation());
        loadedParticipant.setComment(dto.getComment());
        return save(loadedParticipant);
    }

    @Transactional
    public Participant acceptEvent(final ParticipantDTO dto) {
        dto.setInvitationStatus(InvitationStatus.ACCEPTED);
        return saveParticipant(dto);
    }

    @Transactional
    public Participant declineEvent(final ParticipantDTO dto) {
        dto.setInvitationStatus(InvitationStatus.DECLINED);
        dto.setFromDate(null);
        dto.setToDate(null);
        dto.setCatering(false);
        dto.setAccommodation(false);
        return saveParticipant(dto);
    }

    public boolean hasEventToken(final String accessToken) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        final Root<Participant> root = criteriaQuery.from(Participant.class);
        criteriaQuery.select(criteriaBuilder.count(root));
        criteriaQuery.where(criteriaBuilder.equal(root.get("token"), accessToken));
        return 0 != entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    public boolean hasUpcomingEvents() {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        final Root<Event> root = criteriaQuery.from(Event.class);
        final Predicate forActive = criteriaBuilder.equal(root.get("active"), true);
        final Predicate forDate = criteriaBuilder.greaterThanOrEqualTo(root.get("endDate"), new Date());
        criteriaQuery.select(criteriaBuilder.count(root));
        criteriaQuery.where(forActive, forDate);
        return 0 != entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    public Participant getLatestParticipant(final Singer singer) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Participant> criteriaQuery = criteriaBuilder.createQuery(Participant.class);
        final Root<Participant> root = criteriaQuery.from(Participant.class);
        final Join<Participant, Event> eventJoin = root.join("event");
        final Predicate forSinger = criteriaBuilder.equal(root.get("singer"), singer);
        final Predicate forDate = criteriaBuilder.greaterThanOrEqualTo(eventJoin.get("endDate"), new Date());
        criteriaQuery.where(forSinger, forDate);
        criteriaQuery.orderBy(criteriaBuilder.asc(eventJoin.get("startDate")));
        try {
            return entityManager.createQuery(criteriaQuery).setMaxResults(1).getSingleResult();
        } catch (final NoResultException e) {
            LOGGER.info("Next Participant could not be found for singer={}", singer);
            return null;
        }
    }

    public EventDetails getLatestEventView() {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<EventDetails> criteriaQuery = criteriaBuilder.createQuery(EventDetails.class);
        final Root<EventDetails> root = criteriaQuery.from(EventDetails.class);
        criteriaQuery.where(criteriaBuilder.greaterThanOrEqualTo(root.get("endDate"), new Date()));
        criteriaQuery.orderBy(criteriaBuilder.asc(root.get("startDate")));
        try {
            return entityManager.createQuery(criteriaQuery).setMaxResults(1).getSingleResult();
        } catch (final NoResultException e) {
            LOGGER.info("Next Event could not be found");
            return null;
        }
    }

    public Event getLatestEvent() {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Event> criteriaQuery = criteriaBuilder.createQuery(Event.class);
        final Root<Event> root = criteriaQuery.from(Event.class);
        criteriaQuery.where(criteriaBuilder.greaterThanOrEqualTo(root.get("endDate"), new Date()));
        criteriaQuery.orderBy(criteriaBuilder.asc(root.get("startDate")));
        try {
            return entityManager.createQuery(criteriaQuery).setMaxResults(1).getSingleResult();
        } catch (final NoResultException e) {
            LOGGER.info("Next Event could not be found");
            return null;
        }
    }

    public EventDetails getNextEventDetailsView(final Long id) {
        final Date startDate = load(EventDetails.class, id).getStartDate();
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<EventDetails> criteriaQuery = criteriaBuilder.createQuery(EventDetails.class);
        final Root<EventDetails> root = criteriaQuery.from(EventDetails.class);
        final Predicate forStartDate = criteriaBuilder.greaterThan(root.get("startDate"), startDate);
        final Predicate forValidDate = criteriaBuilder.greaterThanOrEqualTo(root.get("startDate"), new Date());
        criteriaQuery.where(forValidDate, forStartDate);
        criteriaQuery.orderBy(criteriaBuilder.asc(root.get("startDate")));
        try {
            return entityManager.createQuery(criteriaQuery).setMaxResults(1).getSingleResult();
        } catch (final NoResultException e) {
            LOGGER.trace("Could not found next event with startDate > {}", startDate);
            return null;
        }
    }

    public EventDetails getPreviousEventDetailsView(final Long id) {
        final Date startDate = load(EventDetails.class, id).getStartDate();
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<EventDetails> criteriaQuery = criteriaBuilder.createQuery(EventDetails.class);
        final Root<EventDetails> root = criteriaQuery.from(EventDetails.class);
        final Predicate forStartDate = criteriaBuilder.lessThan(root.get("startDate"), startDate);
        final Predicate forValidDate = criteriaBuilder.greaterThanOrEqualTo(root.get("startDate"), new Date());
        criteriaQuery.where(forValidDate, forStartDate);
        criteriaQuery.orderBy(criteriaBuilder.desc(root.get("startDate")));
        try {
            return entityManager.createQuery(criteriaQuery).setMaxResults(1).getSingleResult();
        } catch (final NoResultException e) {
            LOGGER.trace("Could not found previous event with startDate < {}", startDate);
            return null;
        }
    }

    /**
     * Returns all upcoming Events from the database.
     *
     * @return List of {@link Event Events}
     */
    public List<Event> getUpcomingEventList() {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Event> criteriaQuery = criteriaBuilder.createQuery(Event.class);
        final Root<Event> root = criteriaQuery.from(Event.class);
        final Predicate forEvents = criteriaBuilder.greaterThanOrEqualTo(root.get("endDate"), new Date());
        criteriaQuery.orderBy(criteriaBuilder.asc(root.<Event>get("startDate")));
        criteriaQuery.where(forEvents);
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    public List<EventDetails> getUpcomingDetailedEventListList() {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<EventDetails> criteriaQuery = criteriaBuilder.createQuery(EventDetails.class);
        final Root<EventDetails> root = criteriaQuery.from(EventDetails.class);
        final Join<EventDetails, Event> eventJoin = root.join("event");
        final Predicate forActive = criteriaBuilder.equal(eventJoin.get("active"), true);
        final Predicate forEndDate = criteriaBuilder.greaterThanOrEqualTo(eventJoin.get("endDate"), new Date());
        criteriaQuery.select(root);
        criteriaQuery.where(forActive, forEndDate);
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    /**
     * Returns a distinct list all event types from all event.
     *
     * @return List of event types
     */
    public List<String> getEventTypeList() {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<String> criteriaQuery = criteriaBuilder.createQuery(String.class);
        final Root<Event> root = criteriaQuery.from(Event.class);
        criteriaQuery.select(root.get("eventType"));
        criteriaQuery.groupBy(root.<String>get("eventType"));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    /**
     * Returns a distinct list all locations from all event.
     *
     * @return List of locations
     */
    public List<String> getLocationList() {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<String> criteriaQuery = criteriaBuilder.createQuery(String.class);
        final Root<Event> root = criteriaQuery.from(Event.class);
        criteriaQuery.select(root.get("location"));
        criteriaQuery.groupBy(root.<String>get("location"));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    /**
     * Returns all upcoming Events with a start offset from the database.
     *
     * @param offset Offset
     * @return List of {@link Event Events}.
     */
    public List<Event> getUpcomingEventListWithOffset(final int offset) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Event> criteriaQuery = criteriaBuilder.createQuery(Event.class);
        final Root<Event> root = criteriaQuery.from(Event.class);
        final Predicate featureEvents = criteriaBuilder.greaterThanOrEqualTo(root.get("endDate"), new Date());
        criteriaQuery.orderBy(criteriaBuilder.asc(root.<Event>get("startDate")));
        criteriaQuery.where(featureEvents);
        return entityManager.createQuery(criteriaQuery).setFirstResult(offset).getResultList();
    }

    public EventDetails getEventDetails(final Event event) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<EventDetails> criteriaQuery = criteriaBuilder.createQuery(EventDetails.class);
        final Root<EventDetails> root = criteriaQuery.from(EventDetails.class);
        criteriaQuery.where(criteriaBuilder.equal(root.get("event"), event));
        try {
            return entityManager.createQuery(criteriaQuery).getSingleResult();
        } catch (final NoResultException e) {
            LOGGER.warn("A view with event_id={} could not be found.", event.getId());
            return null;
        }
    }

    /**
     * Fetches all {@link Participant} where the {@link Event} is present. The result is ordered by
     * {@link Person#lastName}.
     *
     * @param event The {@link Event} to filter for.
     * @return The list of ordered {@link Participant}.
     */
    public List<Participant> getParticipants(final Event event) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Participant> criteriaQuery = criteriaBuilder.createQuery(Participant.class);
        final Root<Participant> root = criteriaQuery.from(Participant.class);
        criteriaQuery.where(criteriaBuilder.equal(root.<Event>get("event"), event));
        criteriaQuery.orderBy(criteriaBuilder.asc(root.join("singer").get("lastName")));
        try {
            return entityManager.createQuery(criteriaQuery).getResultList();
        } catch (Exception e) {
            LOGGER.warn("Mapping between {} and singer could not be found.", event.getName());
            return new ArrayList<>();
        }
    }

    public List<Person> getPersonList(final Event event) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Person> criteriaQuery = criteriaBuilder.createQuery(Person.class);
        final Root<Participant> root = criteriaQuery.from(Participant.class);
        final Join<Participant, Singer> singerJoin = root.join("singer");
        criteriaQuery.select(singerJoin.get("person"));
        criteriaQuery.where(criteriaBuilder.equal(root.get("event"), event));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    /**
     * Fetches all {@link Participant} where the {@link Event} is present and invited. The
     * result is ordered by {@link Person#lastName}.
     *
     * @param event   The {@link Event} to filter for.
     * @param invited Whether the {@link Participant} is not invited.
     * @return The list of ordered {@link Participant}.
     */
    public List<Participant> getParticipants(final Event event, final boolean invited) {
        if (invited) {
            return getInvitedParticipants(event);
        } else {
            return getUninvitedParticipants(event);
        }
    }

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

    public List<Participant> getUninvitedParticipants(final Event event) {
        return getParticipants(event, InvitationStatus.UNINVITED);
    }

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

    public Participant getParticipant(final Singer singer, final Event event) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Participant> criteriaQuery = criteriaBuilder.createQuery(Participant.class);
        final Root<Participant> root = criteriaQuery.from(Participant.class);
        final Predicate forEvent = criteriaBuilder.equal(root.<Event>get("event"), event);
        final Predicate forSinger = criteriaBuilder.equal(root.<Singer>get("singer"), singer);
        criteriaQuery.where(forEvent, forSinger);
        try {
            return entityManager.createQuery(criteriaQuery).getSingleResult();
        } catch (final NoResultException e) {
            LOGGER.warn("Mapping between {} and {} does not exist.", event.getName(), singer.getDisplayName());
            return null;
        }
    }

    public Participant getParticipant(final String email, final Long eventId) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Participant> criteriaQuery = criteriaBuilder.createQuery(Participant.class);
        final Root<Participant> root = criteriaQuery.from(Participant.class);
        final Join<Participant, Event> eventJoin = root.join("event");
        final Join<Participant, Singer> singerJoin = root.join("singer");
        final Predicate forEvent = criteriaBuilder.equal(eventJoin.get("id"), eventId);
        final Predicate forEmail = criteriaBuilder.equal(singerJoin.get("email"), email);
        criteriaQuery.where(forEvent, forEmail);
        try {
            return entityManager.createQuery(criteriaQuery).getSingleResult();
        } catch (final NoResultException e) {
            LOGGER.warn("Mapping between Event with id={} and {} could not be found.", eventId, email);
            return null;
        }
    }

    public Participant getParticipant(final String token) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Participant> criteriaQuery = criteriaBuilder.createQuery(Participant.class);
        final Root<Participant> root = criteriaQuery.from(Participant.class);
        criteriaQuery.where(criteriaBuilder.equal(root.<String>get("token"), token));
        try {
            return entityManager.createQuery(criteriaQuery).getSingleResult();
        } catch (final NoResultException e) {
            LOGGER.warn("An entry for \"{}\" could not be found.", token);
            return null;
        }
    }

    /**
     * Fetches all {@link Participant} where the {@link Singer} is present, is {@link Singer#active} and the
     * {@link Event#endDate} is greater than today. The result is ordered by {@link Event#startDate} and
     * {@link Person#lastName}.
     *
     * @param singer {@link Singer} to filter for.
     * @return The list of ordered {@link Participant}.
     */
    public List<Participant> getParticipants(final Singer singer) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Participant> criteriaQuery = criteriaBuilder.createQuery(Participant.class);
        final Root<Participant> root = criteriaQuery.from(Participant.class);
        final Predicate forSinger = criteriaBuilder.equal(root.get("singer"), singer);
        final Join<Participant, Event> eventJoin = root.join("event");
        final Predicate forEndDate = criteriaBuilder.greaterThanOrEqualTo(eventJoin.get("endDate"), new Date());
        final Predicate forActive = criteriaBuilder.equal(eventJoin.get("active"), true);
        criteriaQuery.where(forSinger, forActive, forEndDate);
        criteriaQuery.orderBy(
            criteriaBuilder.asc(eventJoin.get("startDate")),
            criteriaBuilder.asc(root.join("singer").get("lastName")));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    /**
     * Fetches all {@link Participant} where the {@link Event} is present and the invitation equals
     * {@link InvitationStatus#PENDING}. The result is ordered by {@link Person#lastName}.
     *
     * @param event {@link Event} to filter for.
     * @return The list of ordered {@link Participant}.
     */
    public List<Participant> getPendingParticipants(final Event event) {
        return getParticipants(event, InvitationStatus.PENDING);
    }

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
        } catch (final NoResultException e) {
            LOGGER.info("Token could not be found for singer={} and event={}", singer, event);
            return null;
        }
    }

    public boolean hasParticipant(final Singer singer, final Event event) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        final Root<Participant> root = criteriaQuery.from(Participant.class);
        final Predicate forSinger = criteriaBuilder.equal(root.get("singer"), singer);
        final Predicate forEvent = criteriaBuilder.equal(root.get("event"), event);
        criteriaQuery.select(criteriaBuilder.count(root));
        criteriaQuery.where(forSinger, forEvent);
        return 0 != entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    /**
     * Invites all Singers from participantList to {@link de.vinado.wicket.participate.data.Event}
     *
     * @param event           {@link de.vinado.wicket.participate.data.Event}
     * @param participantList List of {@link Participant} to invite
     * @return Amount of sent emails
     */
    @Transactional
    public int inviteParticipants(final Event event, final List<Participant> participantList, final boolean reminder) {
        int count = 0;
        final ApplicationProperties properties = ParticipateApplication.get().getApplicationProperties();
        final List<MimeMessagePreparator> preparators = new ArrayList<>();

        for (Participant participant : participantList) {
            final Singer singer = participant.getSinger();

            final MailData mailData = new MailData() {
                @Override
                public Map<String, Object> getData() {
                    final Map<String, Object> data = super.getData();
                    data.put("event", event);
                    data.put("singer", singer);
                    data.put("acceptLink", ParticipateUtils.generateInvitationLink(participant.getToken()));
                    return data;
                }
            };

            try {
                mailData.setFrom(properties.getMail().getSender());
                mailData.addTo(singer.getEmail(), singer.getDisplayName());
                mailData.setSubject(event.getName());
            } catch (AddressException e) {
                LOGGER.error("Malformed email address", e);
                continue;
            }

            if (!reminder) {
                mailData.addAttachment(newEventAttachment(event, event.getLocation()));
            }
            // TODO Template erstellen
            if (InvitationStatus.UNINVITED.equals(participant.getInvitationStatus())) {
                preparators.add(emailService.getMimeMessagePreparator(mailData, "fm-eventReminder.ftl", true));
            } else {
                preparators.add(emailService.getMimeMessagePreparator(mailData, "fm-eventInvite.ftl", true));
            }

            final Participant loadedParticipant = load(Participant.class, participant.getId());
            loadedParticipant.setInvitationStatus(InvitationStatus.PENDING);
            save(loadedParticipant);

            count++;
        }

        emailService.send(preparators.toArray(new MimeMessagePreparator[0]));
        return count;
    }

    private EmailAttachment newEventAttachment(final Event event, final String location) {
        final String eventName = event.getEventType() + " in " + location;
        final ApplicationProperties appProperties = ParticipateApplication.get().getApplicationProperties();

        final net.fortuna.ical4j.model.Calendar cal = new net.fortuna.ical4j.model.Calendar();
        cal.getProperties().add(new ProdId(""
            + "-//" + appProperties.getCustomer() + "// "
            + ParticipateApplication.get().getApplicationName() + " " + appProperties.getVersion()
            + "//DE"));
        cal.getProperties().add(Version.VERSION_2_0);
        cal.getProperties().add(CalScale.GREGORIAN);
        cal.getProperties().add(Method.REQUEST);

        final UUID uuid = UUID.randomUUID();
        final VEvent vEvent = new VEvent();
        vEvent.getProperties().add(new Uid(uuid.toString()));
        vEvent.getProperties().add(new Summary(eventName));
        vEvent.getProperties().add(new Description(event.getDescription()));
        vEvent.getProperties().add(new Location(location));
        vEvent.getProperties().add(new Created(new DateTime(event.getCreationDate())));
        vEvent.getProperties().add(vEvent.getDateStamp());


        vEvent.getProperties().add(new SimpleDateProperty(SimpleDateProperty.Type.DTSTART, event.getStartDate()));
        if (!event.getStartDate().equals(event.getEndDate())) {
            vEvent.getProperties().add(new SimpleDateProperty(SimpleDateProperty.Type.DTEND, event.getEndDate()));
        }

        if (null != ParticipateSession.get().getUser().getPerson()) {
            Organizer organizer = new Organizer(URI.create("mailto:" + ParticipateSession.get().getUser().getPerson().getEmail()));
            vEvent.getProperties().add(organizer);
            vEvent.getProperties().getProperty(Property.ORGANIZER).getParameters().add(new Cn(appProperties.getCustomer()));
        }

        cal.getComponents().add(vEvent);

        try {
            return new EmailAttachment("participate-event.ics", "text/calendar",
                new ByteArrayInputStream(cal.toString().getBytes(StandardCharsets.UTF_8.name())));
        } catch (UnsupportedEncodingException e) {
            throw new WicketRuntimeException("UnsupportedEncodingException", e);
        }
    }

    public List<EventDetails> getFilteredEventList(final EventFilter eventFilter) {
        if (null == eventFilter) {
            return getUpcomingDetailedEventListList();
        }

        if (eventFilter.isShowAll()) {
            return getAll(EventDetails.class);
        }

        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<EventDetails> criteriaQuery = criteriaBuilder.createQuery(EventDetails.class);
        final Root<EventDetails> root = criteriaQuery.from(EventDetails.class);

        final List<Predicate> orPredicates = new ArrayList<>();
        final List<Predicate> andPredicates = new ArrayList<>();

        final String searchTerm = eventFilter.getSearchTerm();
        if (!Strings.isEmpty(eventFilter.getSearchTerm())) {
            orPredicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),
                "%" + searchTerm.toLowerCase() + "%"));
            orPredicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("eventType")),
                "%" + searchTerm.toLowerCase() + "%"));
            orPredicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("location")),
                "%" + searchTerm.toLowerCase() + "%"));
        }

        final Date startDate = eventFilter.getStartDate();
        if (null != startDate) {
            // if (event.endDate >= startDate)
            andPredicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("startDate"), startDate));
        } else {
            andPredicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("startDate"), new Date()));
        }

        final Date endDate = eventFilter.getEndDate();
        if (null != endDate) {
            // if (event.endDate <= endDate)
            andPredicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("endDate"), endDate));
        }

        if (!andPredicates.isEmpty() && orPredicates.isEmpty()) {
            criteriaQuery.where(andPredicates.toArray(new Predicate[andPredicates.size()]));
        } else if (andPredicates.isEmpty() && !orPredicates.isEmpty()) {
            /*Predicate disjunction = criteriaBuilder.disjunction();
            disjunction = criteriaBuilder.or(andPredicates.toArray(new Predicate[predicates.size()]));
            criteriaQuery.where(disjunction);*/

            criteriaQuery.where(criteriaBuilder.or(orPredicates.toArray(new Predicate[orPredicates.size()])));
        } else {
            final Predicate and = criteriaBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));
            final Predicate or = criteriaBuilder.or(orPredicates.toArray(new Predicate[orPredicates.size()]));
            criteriaQuery.where(and, or);
        }

        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    /**
     * Fetches all {@link Participant} where the {@link Event} is present. The result is filtered by
     * {@link ParticipantFilter} and ordered by {@link Person#lastName}.
     *
     * @param event  The {@link Event} to filter for.
     * @param filter The filter criteria.
     * @return An filtered and ordered list of {@link Participant}.
     */
    public List<Participant> getFilteredParticipants(final Event event, final ParticipantFilter filter) {
        if (null == filter) {
            return getParticipants(event);
        }

        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Participant> criteriaQuery = criteriaBuilder.createQuery(Participant.class);
        final Root<Participant> root = criteriaQuery.from(Participant.class);
        final Join<Participant, Singer> singerJoin = root.join("singer");

        final List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.equal(root.<Event>get("event"), event));

        final String searchTerm = filter.getSearchTerm();
        if (!Strings.isEmpty(searchTerm))
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(singerJoin.get("displayName")), "%" + searchTerm + "%"));

        final Voice voice = filter.getVoice();
        if (null != voice)
            predicates.add(criteriaBuilder.equal(singerJoin.get("voice"), voice));

        final InvitationStatus invitationStatus = filter.getInvitationStatus();
        if (null != invitationStatus)
            predicates.add(criteriaBuilder.equal(root.get("invitationStatus"), invitationStatus));

        if (filter.isNotInvited()) {
            predicates.add(criteriaBuilder.notEqual(root.get("invitationStatus"), InvitationStatus.UNINVITED));
        }

        criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()]));
        criteriaQuery.orderBy(criteriaBuilder.asc(singerJoin.get("lastName")));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    /**
     * A more in detail filter that fetches all {@link Participant} where the {@link Event} is present. The result
     * is filtered by {@link DetailedParticipantFilter} and ordered by {@link Person#lastName}.
     *
     * @param event  The {@link Event} to filter for.
     * @param filter The filter criteria.
     * @return An filtered and ordered list of {@link Participant}.
     */
    public List<Participant> getDetailedFilteredParticipants(final Event event, final DetailedParticipantFilter filter) {
        if (null == filter) {
            return getParticipants(event);
        }

        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Participant> criteriaQuery = criteriaBuilder.createQuery(Participant.class);
        final Root<Participant> root = criteriaQuery.from(Participant.class);
        final Join<Participant, Singer> singerJoin = root.join("singer");

        final List<Predicate> orPredicates = new ArrayList<>();
        final List<Predicate> andPredicates = new ArrayList<>();
        andPredicates.add(criteriaBuilder.equal(root.<Event>get("event"), event));

        final String name = filter.getName();
        if (!Strings.isEmpty(name))
            andPredicates.add(criteriaBuilder.like(criteriaBuilder.lower(singerJoin.get("searchName")), "%" + name + "%"));

        final String comment = filter.getComment();
        if (!Strings.isEmpty(comment)) {
            orPredicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("comment")), "%" + comment + "%"));
        }

        final Voice voice = filter.getVoice();
        if (null != voice)
            andPredicates.add(criteriaBuilder.equal(singerJoin.get("voice"), voice));

        final InvitationStatus invitationStatus = filter.getInvitationStatus();
        if (null != invitationStatus)
            andPredicates.add(criteriaBuilder.equal(root.get("invitationStatus"), invitationStatus));

        final Date fromDate = filter.getFromDate();
        if (null != fromDate)
            andPredicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("fromDate"), fromDate));

        final Date toDate = filter.getToDate();
        if (null != toDate)
            andPredicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("toDate"), toDate));

        if (filter.isAccommodation())
            andPredicates.add(criteriaBuilder.equal(root.get("accommodation"), true));

        if (filter.isCatering())
            andPredicates.add(criteriaBuilder.equal(root.get("catering"), true));

        if (!andPredicates.isEmpty() && orPredicates.isEmpty()) {
            criteriaQuery.where(andPredicates.toArray(new Predicate[andPredicates.size()]));
        } else if (andPredicates.isEmpty() && !orPredicates.isEmpty()) {
            criteriaQuery.where(criteriaBuilder.or(orPredicates.toArray(new Predicate[orPredicates.size()])));
        } else {
            final Predicate and = criteriaBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));
            final Predicate or = criteriaBuilder.or(orPredicates.toArray(new Predicate[orPredicates.size()]));
            criteriaQuery.where(and, or);
        }

        criteriaQuery.orderBy(criteriaBuilder.asc(singerJoin.get("lastName")));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }
}
