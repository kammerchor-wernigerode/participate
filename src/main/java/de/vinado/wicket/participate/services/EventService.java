package de.vinado.wicket.participate.services;

import de.vinado.wicket.participate.common.ParticipateUtils;
import de.vinado.wicket.participate.configuration.ApplicationProperties;
import de.vinado.wicket.participate.email.Email;
import de.vinado.wicket.participate.email.EmailAttachment;
import de.vinado.wicket.participate.email.service.EmailService;
import de.vinado.wicket.participate.model.Event;
import de.vinado.wicket.participate.model.EventDetails;
import de.vinado.wicket.participate.model.InvitationStatus;
import de.vinado.wicket.participate.model.Participant;
import de.vinado.wicket.participate.model.Person;
import de.vinado.wicket.participate.model.Singer;
import de.vinado.wicket.participate.model.Terminable;
import de.vinado.wicket.participate.model.User;
import de.vinado.wicket.participate.model.Voice;
import de.vinado.wicket.participate.model.dtos.EventDTO;
import de.vinado.wicket.participate.model.dtos.ParticipantDTO;
import de.vinado.wicket.participate.model.filters.DetailedParticipantFilter;
import de.vinado.wicket.participate.model.filters.EventFilter;
import de.vinado.wicket.participate.model.filters.ParticipantFilter;
import de.vinado.wicket.participate.model.ical4j.SimpleDateProperty;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
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
import org.apache.commons.lang3.time.DateUtils;
import org.apache.wicket.util.string.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static com.pivovarit.function.ThrowingFunction.sneaky;
import static de.vinado.wicket.participate.common.DateUtils.convert;
import static java.time.temporal.ChronoUnit.DAYS;

/**
 * The service takes care of event and event related objects.
 *
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
@Slf4j
@Service
@Setter(value = AccessLevel.PROTECTED, onMethod = @__(@Autowired))
public class EventService extends DataService {

    private PersonService personService;
    private EmailService emailService;
    private ApplicationProperties applicationProperties;

    @Setter(AccessLevel.NONE)
    @Value("${spring.application.name:KCH Participate}")
    private String applicationName;

    @Override
    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Creates a new event.
     *
     * @param dto the DTO from which the event is created
     * @return created event
     */
    @Transactional
    public Event createEvent(EventDTO dto) {
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
     * Saves an existing event.
     *
     * @param dto the DTO of the event to be updated
     * @return the saved event
     */
    @Transactional
    public Event saveEvent(EventDTO dto) {
        Event loadedEvent = load(Event.class, dto.getEvent().getId());

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
     * Removes the event.
     *
     * @param event the event to be removed
     */
    @Transactional
    public void removeEvent(Event event) {
        Event loadedEvent = load(Event.class, event.getId());
        loadedEvent.setActive(false);
        save(loadedEvent);
    }

    /**
     * Creates a new participant.
     *
     * @param event  the participant from which the participant is created
     * @param singer the singer to attend the event
     * @return the created participant
     */
    @Transactional
    public Participant createParticipant(Event event, Singer singer) {
        Participant participant = new Participant(event, singer, generateEventToken(20), InvitationStatus.UNINVITED);
        return save(participant);
    }

    /**
     * Generates a new unique event token, by recursively checking for existing tokens.
     *
     * @param length the length of the token to generate
     * @return an unique token
     */
    private String generateEventToken(int length) {
        String accessToken = RandomStringUtils.randomAlphanumeric(length);

        if (hasToken(accessToken)) {
            return generateEventToken(length);
        } else {
            return accessToken;
        }
    }

    /**
     * Saves an existing participant.
     *
     * @param dto the DTO of the participant to be updated
     * @return the saved participant
     */
    @Transactional
    public Participant saveParticipant(ParticipantDTO dto) {
        Participant loadedParticipant = load(Participant.class, dto.getParticipant().getId());
        loadedParticipant.setInvitationStatus(dto.getInvitationStatus());
        loadedParticipant.setFromDate(dto.getFromDate());
        loadedParticipant.setToDate(dto.getToDate());
        loadedParticipant.setCatering(dto.isCatering());
        loadedParticipant.setAccommodation(dto.isAccommodation());
        loadedParticipant.setComment(dto.getComment());
        return save(loadedParticipant);
    }

    /**
     * Changes the invitation status to accepted.
     *
     * @param dto the participant who accepts the invitation
     * @return the saved participant
     */
    @Transactional
    public Participant acceptEvent(ParticipantDTO dto) {
        dto.setInvitationStatus(InvitationStatus.ACCEPTED);
        return saveParticipant(dto);
    }

    /**
     * Changes the invitation status to declined and resets the participant to default.
     *
     * @param dto the participant who declines the invitation
     * @return the saved participant
     */
    @Transactional
    public Participant declineEvent(ParticipantDTO dto) {
        dto.setInvitationStatus(InvitationStatus.DECLINED);
        dto.setFromDate(null);
        dto.setToDate(null);
        dto.setCatering(false);
        dto.setAccommodation(false);
        return saveParticipant(dto);
    }

    /**
     * @param token the participation token to check
     * @return {@code true} if the given token exist; {@code false} otherwise
     */
    public boolean hasToken(String token) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<Participant> root = criteriaQuery.from(Participant.class);
        criteriaQuery.select(criteriaBuilder.count(root));
        criteriaQuery.where(criteriaBuilder.equal(root.get("token"), token));
        return 0 != entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    /**
     * @return {@code true} if an upcoming event exist; {@code false} otherwise
     */
    public boolean hasUpcomingEvents() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<Event> root = criteriaQuery.from(Event.class);
        criteriaQuery.select(criteriaBuilder.count(root));
        criteriaQuery.where(forActive(criteriaBuilder, root), forUpcomingDate(criteriaBuilder, root));
        return 0 != entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    /**
     * Fetches the participant of the latest event.
     *
     * @param singer the participant's singer
     * @return the latest event's participant
     *
     * @throws NoResultException in case the participant could not be found
     */
    public Participant getLatestParticipant(Singer singer) throws NoResultException {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Participant> criteriaQuery = criteriaBuilder.createQuery(Participant.class);
        Root<Participant> root = criteriaQuery.from(Participant.class);
        Join<Participant, Event> eventJoin = root.join("event");
        Predicate forSinger = criteriaBuilder.equal(root.get("singer"), singer);
        criteriaQuery.where(forSinger, forUpcomingDate(criteriaBuilder, eventJoin), forActive(criteriaBuilder, eventJoin));
        criteriaQuery.orderBy(criteriaBuilder.asc(eventJoin.get("startDate")));
        return entityManager.createQuery(criteriaQuery).setMaxResults(1).getSingleResult();
    }

    /**
     * @return the latest event details
     *
     * @throws NoResultException in case an upcoming event doesn't exist
     */
    public EventDetails getLatestEventDetails() throws NoResultException {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<EventDetails> criteriaQuery = criteriaBuilder.createQuery(EventDetails.class);
        Root<EventDetails> root = criteriaQuery.from(EventDetails.class);
        criteriaQuery.where(forUpcomingDate(criteriaBuilder, root));
        return entityManager.createQuery(criteriaQuery).setMaxResults(1).getSingleResult();
    }

    /**
     * @return the latest event
     *
     * @throws NoResultException in case an upcoming event doesn't exist
     */
    public Event getLatestEvent() throws NoResultException {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> criteriaQuery = criteriaBuilder.createQuery(Event.class);
        Root<Event> root = criteriaQuery.from(Event.class);
        criteriaQuery.where(forUpcomingDate(criteriaBuilder, root));
        criteriaQuery.orderBy(criteriaBuilder.asc(root.get("startDate")));
        return entityManager.createQuery(criteriaQuery).setMaxResults(1).getSingleResult();
    }

    /**
     * @param eventDetails the event details on which the successor is determined
     * @return the succeeding event details
     *
     * @throws NoResultException in case the next event could not be found
     */
    public EventDetails getSuccessor(EventDetails eventDetails) throws NoResultException {
        Date startDate = load(EventDetails.class, eventDetails.getId()).getStartDate();

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<EventDetails> criteriaQuery = criteriaBuilder.createQuery(EventDetails.class);
        Root<EventDetails> root = criteriaQuery.from(EventDetails.class);
        Predicate forStartDate = criteriaBuilder.greaterThan(root.get("startDate"), startDate);
        criteriaQuery.where(forUpcomingDate(criteriaBuilder, root), forStartDate);
        return entityManager.createQuery(criteriaQuery).setMaxResults(1).getSingleResult();
    }

    /**
     * @param eventDetails the event details on which the predecessor is determined
     * @return the previous event details
     *
     * @throws NoResultException in case the previous event could not be found
     */
    public EventDetails getPredecessor(EventDetails eventDetails) throws NoResultException {
        Date startDate = load(EventDetails.class, eventDetails.getId()).getStartDate();

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<EventDetails> criteriaQuery = criteriaBuilder.createQuery(EventDetails.class);
        Root<EventDetails> root = criteriaQuery.from(EventDetails.class);
        Predicate forStartDate = criteriaBuilder.lessThan(root.get("startDate"), startDate);
        criteriaQuery.where(forUpcomingDate(criteriaBuilder, root), forStartDate);
        criteriaQuery.orderBy(criteriaBuilder.desc(root.get("startDate")));
        return entityManager.createQuery(criteriaQuery).setMaxResults(1).getSingleResult();
    }

    /**
     * @return list of upcoming events
     */
    public List<Event> getUpcomingEvents() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> criteriaQuery = criteriaBuilder.createQuery(Event.class);
        Root<Event> root = criteriaQuery.from(Event.class);
        criteriaQuery.orderBy(criteriaBuilder.asc(root.<Event>get("startDate")));
        criteriaQuery.where(forUpcomingDate(criteriaBuilder, root));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    /**
     * @return list of upcoming event details
     */
    public List<EventDetails> getUpcomingEventDetails() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<EventDetails> criteriaQuery = criteriaBuilder.createQuery(EventDetails.class);
        Root<EventDetails> root = criteriaQuery.from(EventDetails.class);
        Join<EventDetails, Event> eventJoin = root.join("event");
        criteriaQuery.where(forActive(criteriaBuilder, eventJoin), forUpcomingDate(criteriaBuilder, eventJoin));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    /**
     * @return list of previous event types
     */
    public List<String> getEventTypes() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<String> criteriaQuery = criteriaBuilder.createQuery(String.class);
        Root<Event> root = criteriaQuery.from(Event.class);
        criteriaQuery.select(root.get("eventType"));
        criteriaQuery.groupBy(root.<String>get("eventType"));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    /**
     * @return list of previous event locations
     */
    public List<String> getLocationList() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<String> criteriaQuery = criteriaBuilder.createQuery(String.class);
        Root<Event> root = criteriaQuery.from(Event.class);
        criteriaQuery.select(root.get("location"));
        criteriaQuery.groupBy(root.<String>get("location"));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    /**
     * Retrieves event details for the given event.
     *
     * @param event the event for which the details should be fetched
     * @return the event details for the event
     *
     * @throws NoResultException in case the event details could not be found
     */
    public EventDetails getEventDetails(Event event) throws NoResultException {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<EventDetails> criteriaQuery = criteriaBuilder.createQuery(EventDetails.class);
        Root<EventDetails> root = criteriaQuery.from(EventDetails.class);
        criteriaQuery.where(criteriaBuilder.equal(root.get("event"), event));
        return entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    /**
     * Retrieves all participants for the given event.
     *
     * @param event the event for which the participants should be fetched
     * @return list of participants
     */
    public List<Participant> getParticipants(Event event) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Participant> criteriaQuery = criteriaBuilder.createQuery(Participant.class);
        Root<Participant> root = criteriaQuery.from(Participant.class);
        criteriaQuery.where(criteriaBuilder.equal(root.<Event>get("event"), event));
        criteriaQuery.orderBy(criteriaBuilder.asc(root.join("singer").get("lastName")));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    /**
     * Retrieves invited participants for the given event.
     *
     * @param event the event for which the participants should be fetched
     * @return list of invited participants
     */
    public List<Participant> getInvitedParticipants(Event event) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Participant> criteriaQuery = criteriaBuilder.createQuery(Participant.class);
        Root<Participant> root = criteriaQuery.from(Participant.class);
        Predicate forEvent = criteriaBuilder.equal(root.<Event>get("event"), event);
        Predicate forInvitationStatus = criteriaBuilder.notEqual(root.get("invitationStatus"), InvitationStatus.UNINVITED);
        criteriaQuery.where(forEvent, forInvitationStatus);
        criteriaQuery.orderBy(criteriaBuilder.asc(root.join("singer").get("lastName")));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    /**
     * Retrieves uninvited participants for the given event.
     *
     * @param event the event for which the participants should be fetched
     * @return list of uninvited participants
     */
    public List<Participant> getUninvitedParticipants(Event event) {
        return getParticipants(event, InvitationStatus.UNINVITED);
    }

    /**
     * Retrieves participants for the given event.
     *
     * @param event            the event for which the participants should be fetched
     * @param invitationStatus the invitation for which the participants should be fetched
     * @return list of participants
     */
    public List<Participant> getParticipants(Event event, InvitationStatus invitationStatus) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Participant> criteriaQuery = criteriaBuilder.createQuery(Participant.class);
        Root<Participant> root = criteriaQuery.from(Participant.class);
        Predicate forEvent = criteriaBuilder.equal(root.<Event>get("event"), event);
        Predicate forInvitationStatus = criteriaBuilder.equal(root.get("invitationStatus"), invitationStatus);
        criteriaQuery.where(forEvent, forInvitationStatus);
        criteriaQuery.orderBy(criteriaBuilder.asc(root.join("singer").get("lastName")));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    /**
     * @param event the event to check if a participant exists
     * @return {@code true} if an participant exist; {@code false} otherwise
     */
    public boolean hasParticipant(Event event) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<Participant> root = criteriaQuery.from(Participant.class);
        Predicate forEvent = criteriaBuilder.equal(root.get("event"), event);
        Predicate forInvited = criteriaBuilder.notEqual(root.get("invitationStatus"), InvitationStatus.UNINVITED);
        criteriaQuery.select(criteriaBuilder.count(root));
        criteriaQuery.where(forInvited, forEvent);
        return 0 != entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    /**
     * Fetches a participant for its token.
     *
     * @param token the token to get the participant of
     * @return the participant
     *
     * @throws NoResultException in case the participant could not be found
     */
    public Participant getParticipant(String token) throws NoResultException {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Participant> criteriaQuery = criteriaBuilder.createQuery(Participant.class);
        Root<Participant> root = criteriaQuery.from(Participant.class);
        criteriaQuery.where(criteriaBuilder.equal(root.<String>get("token"), token));
        return entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    /**
     * Fetches all participants where the singer is present, active and the end date is greater than today.
     *
     * @param singer the singer for which all its participant should be fetched
     * @return list of participants
     */
    public List<Participant> getParticipants(Singer singer) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Participant> criteriaQuery = criteriaBuilder.createQuery(Participant.class);
        Root<Participant> root = criteriaQuery.from(Participant.class);
        Predicate forSinger = criteriaBuilder.equal(root.get("singer"), singer);
        Join<Participant, Event> eventJoin = root.join("event");
        criteriaQuery.where(forSinger, forActive(criteriaBuilder, eventJoin), forUpcomingDate(criteriaBuilder, eventJoin));
        criteriaQuery.orderBy(
            criteriaBuilder.asc(eventJoin.get("startDate")),
            criteriaBuilder.asc(root.join("singer").get("lastName")));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    /**
     * Sends an invitation to all participating singers of an event.
     *
     * @param participants the list of participant to invite
     * @param organizer    the organizer
     * @return the amount of sent emails
     */
    @Transactional
    public int inviteParticipants(List<Participant> participants, User organizer) {
        Stream<Email> invitations = participants
            .stream()
            .map(sneaky(participant -> {
                Singer singer = participant.getSinger();

                Email email = new Email() {

                    public Map<String, Object> getData(ApplicationProperties properties) {
                        Map<String, Object> data = super.getData(properties);
                        data.put("event", participant.getEvent());
                        data.put("singer", singer);
                        data.put("acceptLink", ParticipateUtils.generateInvitationLink(
                            properties.getBaseUrl(),
                            participant.getToken())
                        );
                        data.put("deadline", DateUtils.addDays(participant.getEvent().getStartDate(), -14));

                        return data;
                    }
                };

                email.setFrom(applicationProperties.getMail().getSender(), applicationProperties.getCustomer());
                email.addTo(singer.getEmail(), singer.getDisplayName());
                email.setSubject(participant.getEvent().getName());
                email.setAttachments(Collections.singleton(newEventAttachment(participant.getEvent(), organizer)));

                if (InvitationStatus.UNINVITED.equals(participant.getInvitationStatus())) {
                    Participant loadedParticipant = load(Participant.class, participant.getId());
                    loadedParticipant.setInvitationStatus(InvitationStatus.PENDING);
                    save(loadedParticipant);
                }

                return email;
            }));

        // TODO Create notification template
        emailService.send(invitations, "inviteSinger-txt.ftl", "inviteSinger-html.ftl");
        return participants.size();
    }

    /**
     * Sends an invitation to the given participant.
     *
     * @param participant the participant to invite
     * @param organizer   the organizer
     */
    @Transactional
    public void inviteParticipant(Participant participant, User organizer) {
        inviteParticipants(Collections.singletonList(participant), organizer);
    }

    /**
     * Creates a new iCal attachment for event invitations.
     *
     * @param event the event with information used in the attachment
     * @return a new email attachment
     */
    private EmailAttachment newEventAttachment(Event event, User organizer) {
        String eventName = event.getEventType() + " in " + event.getLocation();

        net.fortuna.ical4j.model.Calendar cal = new net.fortuna.ical4j.model.Calendar();
        cal.getProperties().add(new ProdId(String.format(
            "-//%s//%s %s//DE",
            applicationProperties.getCustomer(),
            applicationName,
            applicationProperties.getVersion()
        )));
        cal.getProperties().add(Version.VERSION_2_0);
        cal.getProperties().add(CalScale.GREGORIAN);
        cal.getProperties().add(Method.REQUEST);

        UUID uuid = UUID.randomUUID();
        VEvent vEvent = new VEvent();
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
        Organizer vOrganizer = new Organizer(URI.create("mailto:" + orgaEmail));
        vEvent.getProperties().add(vOrganizer);
        vEvent.getProperties().getProperty(Property.ORGANIZER).getParameters().add(new Cn(applicationProperties.getCustomer()));

        cal.getComponents().add(vEvent);

        return new EmailAttachment("participate-event.ics", MediaType.valueOf("text/calendar"), cal.toString().getBytes());
    }

    /**
     * Fetches all event details that matches the given filter.
     *
     * @param eventFilter the filter to apply to the event details
     * @return list of filtered event details
     */
    public List<EventDetails> getFilteredEventDetails(EventFilter eventFilter) {
        if (null == eventFilter) {
            return getUpcomingEventDetails();
        }

        if (eventFilter.isShowAll()) {
            return getAll(EventDetails.class);
        }

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<EventDetails> criteriaQuery = criteriaBuilder.createQuery(EventDetails.class);
        Root<EventDetails> root = criteriaQuery.from(EventDetails.class);

        List<Predicate> orPredicates = new ArrayList<>();
        List<Predicate> andPredicates = new ArrayList<>();

        String searchTerm = eventFilter.getSearchTerm();
        if (!Strings.isEmpty(eventFilter.getSearchTerm())) {
            orPredicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),
                "%" + searchTerm.toLowerCase() + "%"));
            orPredicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("eventType")),
                "%" + searchTerm.toLowerCase() + "%"));
            orPredicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("location")),
                "%" + searchTerm.toLowerCase() + "%"));
        }

        Date startDate = eventFilter.getStartDate();
        if (null != startDate) {
            // if (event.endDate >= startDate)
            andPredicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("startDate"), startDate));
        } else {
            andPredicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("startDate"), new Date()));
        }

        Date endDate = eventFilter.getEndDate();
        if (null != endDate) {
            // if (event.endDate <= endDate)
            andPredicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("endDate"), endDate));
        }

        if (!andPredicates.isEmpty() && orPredicates.isEmpty()) {
            criteriaQuery.where(andPredicates.toArray(new Predicate[0]));
        } else if (andPredicates.isEmpty() && !orPredicates.isEmpty()) {
            criteriaQuery.where(criteriaBuilder.or(orPredicates.toArray(new Predicate[0])));
        } else {
            Predicate and = criteriaBuilder.and(andPredicates.toArray(new Predicate[0]));
            Predicate or = criteriaBuilder.or(orPredicates.toArray(new Predicate[0]));
            criteriaQuery.where(and, or);
        }

        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    /**
     * Fetches all participant where the event is present. The result is filtered by the given filter.
     *
     * @param event  the event for which the participants should be fetched
     * @param filter the filter to apply to the participants
     * @return list of filtered participants
     */
    public List<Participant> getFilteredParticipants(Event event, ParticipantFilter filter) {
        if (null == filter) {
            return getParticipants(event);
        }

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Participant> criteriaQuery = criteriaBuilder.createQuery(Participant.class);
        Root<Participant> root = criteriaQuery.from(Participant.class);
        Join<Participant, Singer> singerJoin = root.join("singer");

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.equal(root.<Event>get("event"), event));

        String searchTerm = filter.getSearchTerm();
        if (!Strings.isEmpty(searchTerm))
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(singerJoin.get("displayName")), "%" + searchTerm + "%"));

        Voice voice = filter.getVoice();
        if (null != voice)
            predicates.add(criteriaBuilder.equal(singerJoin.get("voice"), voice));

        InvitationStatus invitationStatus = filter.getInvitationStatus();
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
     * Fetches all participant where the event is present. The result is filtered by the given filter.
     *
     * @param event  the event for which the participants should be fetched
     * @param filter the filter to apply to the participants
     * @return list of detailed filtered participants
     */
    public List<Participant> getDetailedFilteredParticipants(Event event, DetailedParticipantFilter filter) {
        if (null == filter) {
            return getParticipants(event);
        }

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Participant> criteriaQuery = criteriaBuilder.createQuery(Participant.class);
        Root<Participant> root = criteriaQuery.from(Participant.class);
        Join<Participant, Singer> singerJoin = root.join("singer");

        List<Predicate> orPredicates = new ArrayList<>();
        List<Predicate> andPredicates = new ArrayList<>();
        andPredicates.add(criteriaBuilder.equal(root.<Event>get("event"), event));

        String name = filter.getName();
        if (!Strings.isEmpty(name))
            andPredicates.add(criteriaBuilder.like(criteriaBuilder.lower(singerJoin.get("searchName")), "%" + name + "%"));

        String comment = filter.getComment();
        if (!Strings.isEmpty(comment)) {
            orPredicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("comment")), "%" + comment + "%"));
        }

        Voice voice = filter.getVoice();
        if (null != voice)
            andPredicates.add(criteriaBuilder.equal(singerJoin.get("voice"), voice));

        InvitationStatus invitationStatus = filter.getInvitationStatus();
        if (null != invitationStatus)
            andPredicates.add(criteriaBuilder.equal(root.get("invitationStatus"), invitationStatus));

        Date fromDate = filter.getFromDate();
        if (null != fromDate)
            andPredicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("fromDate"), fromDate));

        Date toDate = filter.getToDate();
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
            Predicate and = criteriaBuilder.and(andPredicates.toArray(new Predicate[0]));
            Predicate or = criteriaBuilder.or(orPredicates.toArray(new Predicate[0]));
            criteriaQuery.where(and, or);
        }

        criteriaQuery.orderBy(criteriaBuilder.asc(singerJoin.get("lastName")));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    /**
     * Retrieves all participants for the given event.
     *
     * @param event   the event for which the participants should be fetched
     * @param invited whether the participants are invited
     * @return list of participants
     */
    public List<Participant> getParticipants(Event event, boolean invited) {
        return invited ? getInvitedParticipants(event) : getUninvitedParticipants(event);
    }

    /**
     * @param participant the participant on which to determine whether the deadline has passed
     * @return {@code true} if the the given participant missed the deadline; {@code false} otherwise
     */
    public boolean hasDeadlineExpired(Participant participant) {
        if (0 > applicationProperties.getDeadlineOffset()) {
            return false;
        }

        LocalDate now = LocalDate.now();
        Date startDate = participant.getEvent().getStartDate();
        return applicationProperties.getDeadlineOffset() >= DAYS.between(now, convert(startDate));
    }

    /**
     * Returns a configured end-date-predicate
     *
     * @param criteriaBuilder Java persistence criteria builder
     * @param entity          the entity for which the end date should be greater than or equal
     * @return upcoming-date-predicate
     */
    private Predicate forUpcomingDate(CriteriaBuilder criteriaBuilder, Path<? extends Terminable> entity) {
        return criteriaBuilder.greaterThanOrEqualTo(entity.get("endDate"), new Date());
    }
}
