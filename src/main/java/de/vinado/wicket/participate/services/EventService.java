package de.vinado.wicket.participate.services;

import de.vinado.wicket.participate.ParticipateApplication;
import de.vinado.wicket.participate.ParticipateSession;
import de.vinado.wicket.participate.common.ParticipateUtils;
import de.vinado.wicket.participate.configuration.ApplicationProperties;
import de.vinado.wicket.participate.model.Event;
import de.vinado.wicket.participate.model.EventDetails;
import de.vinado.wicket.participate.model.InvitationStatus;
import de.vinado.wicket.participate.model.Participant;
import de.vinado.wicket.participate.model.Person;
import de.vinado.wicket.participate.model.Singer;
import de.vinado.wicket.participate.model.Terminable;
import de.vinado.wicket.participate.model.Voice;
import de.vinado.wicket.participate.model.dtos.EventDTO;
import de.vinado.wicket.participate.model.dtos.ParticipantDTO;
import de.vinado.wicket.participate.model.email.EmailAttachment;
import de.vinado.wicket.participate.model.email.MailData;
import de.vinado.wicket.participate.model.filters.DetailedParticipantFilter;
import de.vinado.wicket.participate.model.filters.EventFilter;
import de.vinado.wicket.participate.model.filters.ParticipantFilter;
import de.vinado.wicket.participate.model.ical4j.SimpleDateProperty;
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
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
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

    @PersistenceContext
    public void setEntityManager(final EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Creates a new {@link de.vinado.wicket.participate.model.Event}.
     *
     * @param dto {@link EventDTO}
     * @return Saved {@link de.vinado.wicket.participate.model.Event}
     */
    @Transactional
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
     * Saves an existing {@link de.vinado.wicket.participate.model.Event}.
     *
     * @param dto {@link EventDTO}
     * @return Saved {@link de.vinado.wicket.participate.model.Event}
     */
    @Transactional
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
     * Sets the {@link Event} to inactive.
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
     * Creates a new {@link Participant}.
     *
     * @param event  {@link Event}
     * @param singer {@link Singer}
     * @return Saved {@link Participant}
     */
    @Transactional
    public Participant createParticipant(final Event event, final Singer singer) {
        final Participant participant = new Participant(event, singer, generateEventToken(20), InvitationStatus.UNINVITED);
        return save(participant);
    }

    private String generateEventToken(final int length) {
        final String accessToken = RandomStringUtils.randomAlphanumeric(length);

        if (hasToken(accessToken)) {
            return generateEventToken(length);
        } else {
            return accessToken;
        }
    }

    /**
     * Saves an existing {@link Participant}.
     *
     * @param dto {@link ParticipantDTO}
     * @return Saved {@link Participant}
     */
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

    /**
     * Changes the {@link InvitationStatus} to {@link InvitationStatus#ACCEPTED}.
     *
     * @param dto {@link ParticipantDTO}
     * @return Saved {@link Participant}
     */
    @Transactional
    public Participant acceptEvent(final ParticipantDTO dto) {
        dto.setInvitationStatus(InvitationStatus.ACCEPTED);
        return saveParticipant(dto);
    }

    /**
     * Changes the {@link InvitationStatus} to {@link InvitationStatus#DECLINED} and resets the {@link Participant} to
     * default.
     *
     * @param dto {@link ParticipantDTO}
     * @return Saved {@link Participant}
     */
    @Transactional
    public Participant declineEvent(final ParticipantDTO dto) {
        dto.setInvitationStatus(InvitationStatus.DECLINED);
        dto.setFromDate(null);
        dto.setToDate(null);
        dto.setCatering(false);
        dto.setAccommodation(false);
        return saveParticipant(dto);
    }

    /**
     * Returns whether the {@link Participant#token} exists.
     *
     * @param token {@link Participant#token}
     * @return Whether the {@link Participant#token} exists.
     */
    public boolean hasToken(final String token) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        final Root<Participant> root = criteriaQuery.from(Participant.class);
        criteriaQuery.select(criteriaBuilder.count(root));
        criteriaQuery.where(criteriaBuilder.equal(root.get("token"), token));
        return 0 != entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    /**
     * Returns whether upcoming {@link Event}s exits.
     *
     * @return Whether upcoming {@link Event}s exits.
     */
    public boolean hasUpcomingEvents() {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        final Root<Event> root = criteriaQuery.from(Event.class);
        criteriaQuery.select(criteriaBuilder.count(root));
        criteriaQuery.where(forActive(criteriaBuilder, root), forUpcomingDate(criteriaBuilder, root));
        return 0 != entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    /**
     * Fetches the {@link Participant} of the latest {@link Event} for the given {@link Singer}.
     *
     * @param singer {@link Singer}
     * @return {@link Participant} of its latest {@link Event}
     */
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
            LOGGER.trace("Could not find Participant of latest event for Singer /w id={}", singer.getId());
            return null;
        }
    }

    /**
     * Fetches the latest {@link EventDetails}.
     *
     * @return Latest {@link EventDetails}
     */
    public EventDetails getLatestEventDetails() {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<EventDetails> criteriaQuery = criteriaBuilder.createQuery(EventDetails.class);
        final Root<EventDetails> root = criteriaQuery.from(EventDetails.class);
        criteriaQuery.where(forUpcomingDate(criteriaBuilder, root));
        try {
            return entityManager.createQuery(criteriaQuery).setMaxResults(1).getSingleResult();
        } catch (NoResultException e) {
            LOGGER.trace("Could not find any upcoming Event");
            return null;
        }
    }

    /**
     * Fetches the latest {@link Event}.
     *
     * @return Latest {@link Event}
     */
    public Event getLatestEvent() {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Event> criteriaQuery = criteriaBuilder.createQuery(Event.class);
        final Root<Event> root = criteriaQuery.from(Event.class);
        criteriaQuery.where(forUpcomingDate(criteriaBuilder, root));
        criteriaQuery.orderBy(criteriaBuilder.asc(root.get("startDate")));
        try {
            return entityManager.createQuery(criteriaQuery).setMaxResults(1).getSingleResult();
        } catch (NoResultException e) {
            LOGGER.trace("Could not find any upcoming Event");
            return null;
        }
    }

    /**
     * Fetches the succeeding of {@link EventDetails}.
     *
     * @param eventDetails {@link EventDetails}
     * @return Next {@link EventDetails}
     */
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
            LOGGER.trace("Could not find successor of Event /w id={}", eventDetails.getEvent().getId());
            return null;
        }
    }

    /**
     * Fetches the preceding of {@link EventDetails}.
     *
     * @param eventDetails {@link EventDetails}
     * @return Previous {@link EventDetails}
     */
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
            LOGGER.trace("Could not find predecessor of Event /w id={}", eventDetails.getEvent().getId());
            return null;
        }
    }

    /**
     * Fetches all upcoming {@link Event}s, sorted by {@link Event#startDate}.
     *
     * @return Upcoming list {@link Event}s
     */
    public List<Event> getUpcomingEvents() {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Event> criteriaQuery = criteriaBuilder.createQuery(Event.class);
        final Root<Event> root = criteriaQuery.from(Event.class);
        criteriaQuery.orderBy(criteriaBuilder.asc(root.<Event>get("startDate")));
        criteriaQuery.where(forUpcomingDate(criteriaBuilder, root));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    /**
     * Fetches all upcoming {@link EventDetails}
     *
     * @return Upcoming list {@link EventDetails}
     */
    public List<EventDetails> getUpcomingEventDetails() {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<EventDetails> criteriaQuery = criteriaBuilder.createQuery(EventDetails.class);
        final Root<EventDetails> root = criteriaQuery.from(EventDetails.class);
        final Join<EventDetails, Event> eventJoin = root.join("event");
        criteriaQuery.where(forActive(criteriaBuilder, eventJoin), forUpcomingDate(criteriaBuilder, eventJoin));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    /**
     * Fetches all grouped {@link Event#eventType}s.
     *
     * @return List of grouped {@link Event#eventType}s
     */
    public List<String> getEventTypes() {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<String> criteriaQuery = criteriaBuilder.createQuery(String.class);
        final Root<Event> root = criteriaQuery.from(Event.class);
        criteriaQuery.select(root.get("eventType"));
        criteriaQuery.groupBy(root.<String>get("eventType"));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    /**
     * Fetches all grouped {@link Event#location}s.
     *
     * @return List of grouped {@link Event#location}s
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
     * Fetches all upcoming {@link Event}s with an start offset sorted by {@link Event#startDate}.
     *
     * @param offset Offset
     * @return List of {@link Event Events}.
     */
    public List<Event> getUpcomingEvents(final int offset) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Event> criteriaQuery = criteriaBuilder.createQuery(Event.class);
        final Root<Event> root = criteriaQuery.from(Event.class);
        criteriaQuery.orderBy(criteriaBuilder.asc(root.<Event>get("startDate")));
        criteriaQuery.where(forUpcomingDate(criteriaBuilder, root));
        return entityManager.createQuery(criteriaQuery).setFirstResult(offset).getResultList();
    }

    /**
     * Fetches an {@link EventDetails} for an {@link Event}.
     *
     * @param event {@link Event}
     * @return {@link EventDetails} for {@link Event}
     */
    public EventDetails getEventDetails(final Event event) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<EventDetails> criteriaQuery = criteriaBuilder.createQuery(EventDetails.class);
        final Root<EventDetails> root = criteriaQuery.from(EventDetails.class);
        criteriaQuery.where(criteriaBuilder.equal(root.get("event"), event));
        try {
            return entityManager.createQuery(criteriaQuery).getSingleResult();
        } catch (NoResultException e) {
            LOGGER.trace("Could not find Event Details for Event /w id={}", event.getId());
            return null;
        }
    }

    /**
     * Fetches all {@link Participant}s where the {@link Event} is present. The result is ordered by
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

    /**
     * Fetches all invited {@link Participant}s of the given {@link Event} ordered by {@link Participant#singer}s
     * {@link Singer#lastName}.
     *
     * @param event {@link Event}
     * @return List of invited {@link Participant}s
     */
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
     * Fetches all {@link InvitationStatus#UNINVITED} {@link Participant}s of the given {@link Event} ordered by
     * {@link Participant#singer}s {@link Singer#lastName}.
     *
     * @param event {@link Event}
     * @return List of {@link InvitationStatus#UNINVITED} {@link Participant}s
     */
    public List<Participant> getUninvitedParticipants(final Event event) {
        return getParticipants(event, InvitationStatus.UNINVITED);
    }

    /**
     * Fetches all {@link Participant}s for {@link InvitationStatus} of the given {@link Event}, ordered by
     * {@link Participant#singer}s {@link Singer#lastName}.
     *
     * @param event            {@link Event}
     * @param invitationStatus {@link InvitationStatus}
     * @return List of {@link Participant}s
     */
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
     * Returns whether any {@link Participant} exists for the given {@link Event}.
     *
     * @param event {@link Event}
     * @return Whether any {@link Participant} exists for the given {@link Event}
     */
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
     * Fetches a {@link Participant} for {@link Event} and {@link Singer}.
     *
     * @param singer {@link Singer}
     * @param event  {@link Event}
     * @return {@link Participant} for {@link Event} and {@link Singer}
     */
    public Participant getParticipant(final Singer singer, final Event event) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Participant> criteriaQuery = criteriaBuilder.createQuery(Participant.class);
        final Root<Participant> root = criteriaQuery.from(Participant.class);
        final Predicate forEvent = criteriaBuilder.equal(root.<Event>get("event"), event);
        final Predicate forSinger = criteriaBuilder.equal(root.<Singer>get("singer"), singer);
        criteriaQuery.where(forEvent, forSinger);
        try {
            return entityManager.createQuery(criteriaQuery).getSingleResult();
        } catch (NoResultException e) {
            LOGGER.trace("Could not find any Participant for Event /w id={} and Singer /w id={}", event.getId(), singer.getId());
            return null;
        }
    }

    /**
     * Fetches a {@link Participant} for {@link Singer#email} and {@link Event#id}.
     *
     * @param email   {@link Singer#email}
     * @param eventId {@link Event#id}
     * @return {@link Participant} for {@link Singer#email} and {@link Event#id}.
     */
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
        } catch (NoResultException e) {
            LOGGER.trace("Could not find any participant for Event /w id={} and Singer /w email=****", eventId);
            return null;
        }
    }

    /**
     * Fetches a {@link Participant} for its {@link Participant#token}.
     *
     * @param token {@link Participant#token}
     * @return {@link Participant} for its {@link Participant#token}
     */
    public Participant getParticipant(final String token) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Participant> criteriaQuery = criteriaBuilder.createQuery(Participant.class);
        final Root<Participant> root = criteriaQuery.from(Participant.class);
        criteriaQuery.where(criteriaBuilder.equal(root.<String>get("token"), token));
        try {
            return entityManager.createQuery(criteriaQuery).getSingleResult();
        } catch (NoResultException e) {
            LOGGER.trace("Could not find Participant /w token={}", token);
            return null;
        }
    }

    /**
     * Fetches all {@link Participant}s where the {@link Singer} is present, is {@link Singer#active} and the
     * {@link Event#endDate} is greater than today. The result is ordered by {@link Event#startDate} and
     * {@link Person#lastName}.
     *
     * @param singer {@link Singer} to filter for.
     * @return List of ordered {@link Participant}s.
     */
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
     * Fetches all {@link Participant}s where the {@link Event} is present and the {@link Participant#invitationStatus}
     * equals {@link InvitationStatus#PENDING}. The result is ordered by {@link Person#lastName}.
     *
     * @param event {@link Event} to filter for.
     * @return List of ordered {@link Participant}s.
     */
    public List<Participant> getPendingParticipants(final Event event) {
        return getParticipants(event, InvitationStatus.PENDING);
    }

    /**
     * Fetches the {@link Participant#token} for its {@link Singer} and {@link Event}.
     *
     * @param singer {@link Singer}
     * @param event  {@link Event}
     * @return {@link Participant#token} for its {@link Singer} and {@link Event}
     */
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
            LOGGER.trace("Could not find Participant for Singer /w id={} and Event /w id={}", singer, event);
            return null;
        }
    }

    /**
     * Returns whether any {@link Participant} exists for {@link Participant#singer} and {@link Participant#event}.
     *
     * @param singer {@link Singer}
     * @param event  {@link Event}
     * @return Whether any {@link Participant} exists for {@link Participant#singer} and {@link Participant#event}
     */
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
     * Sends an invitation to all participating {@link Singer}s of an {@link Event}.
     *
     * @param participants List of {@link Participant}s to invite
     * @return Amount of sent emails
     */
    @Transactional
    public int inviteParticipants(final List<Participant> participants) {
        int count = 0;
        final List<MimeMessagePreparator> preparators = new ArrayList<>();

        for (Participant participant : participants) {
            final Singer singer = participant.getSinger();

            final MailData mailData = new MailData() {
                @Override
                public Map<String, Object> getData() {
                    final Map<String, Object> data = super.getData();
                    data.put("event", participant.getEvent());
                    data.put("singer", singer);
                    data.put("acceptLink", ParticipateUtils.generateInvitationLink(participant.getToken()));
                    return data;
                }
            };

            try {
                mailData.setFrom(ParticipateApplication.get().getApplicationProperties().getMail().getSender());
                mailData.addTo(singer.getEmail(), singer.getDisplayName());
                mailData.setSubject(participant.getEvent().getName());
            } catch (AddressException e) {
                LOGGER.error("Malformed email address", e);
                continue;
            }

            // TODO Create notification template
            if (InvitationStatus.UNINVITED.equals(participant.getInvitationStatus())) {
                mailData.addAttachment(newEventAttachment(participant.getEvent()));
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

    /**
     * Sends an invitation to the given {@link Participant}
     *
     * @param participant {@link Participant}
     */
    public void inviteParticipant(final Participant participant) {
        inviteParticipants(Collections.singletonList(participant));
    }

    private EmailAttachment newEventAttachment(final Event event) {
        final String eventName = event.getEventType() + " in " + event.getLocation();
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
        vEvent.getProperties().add(new Location(event.getLocation()));
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

    /**
     * Fetches all {@link EventDetails} that matches the {@link EventFilter}.
     *
     * @param eventFilter {@link EventFilter}
     * @return List of filtered {@link EventDetails}
     */
    public List<EventDetails> getFilteredEventDetails(final EventFilter eventFilter) {
        if (null == eventFilter) {
            return getUpcomingEventDetails();
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
            criteriaQuery.where(andPredicates.toArray(new Predicate[0]));
        } else if (andPredicates.isEmpty() && !orPredicates.isEmpty()) {
            criteriaQuery.where(criteriaBuilder.or(orPredicates.toArray(new Predicate[0])));
        } else {
            final Predicate and = criteriaBuilder.and(andPredicates.toArray(new Predicate[0]));
            final Predicate or = criteriaBuilder.or(orPredicates.toArray(new Predicate[0]));
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

        criteriaQuery.where(predicates.toArray(new Predicate[0]));
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
            criteriaQuery.where(andPredicates.toArray(new Predicate[0]));
        } else if (andPredicates.isEmpty() && !orPredicates.isEmpty()) {
            criteriaQuery.where(criteriaBuilder.or(orPredicates.toArray(new Predicate[0])));
        } else {
            final Predicate and = criteriaBuilder.and(andPredicates.toArray(new Predicate[0]));
            final Predicate or = criteriaBuilder.or(orPredicates.toArray(new Predicate[0]));
            criteriaQuery.where(and, or);
        }

        criteriaQuery.orderBy(criteriaBuilder.asc(singerJoin.get("lastName")));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    private Predicate forUpcomingDate(final CriteriaBuilder criteriaBuilder, final Path<? extends Terminable> eventPath) {
        return criteriaBuilder.greaterThanOrEqualTo(eventPath.get("endDate"), new Date());
    }
}
