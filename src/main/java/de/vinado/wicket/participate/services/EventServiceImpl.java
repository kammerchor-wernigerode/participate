package de.vinado.wicket.participate.services;

import de.vinado.app.participate.event.app.CalendarUrl;
import de.vinado.app.participate.event.model.EventName;
import de.vinado.app.participate.notification.email.app.EmailService;
import de.vinado.app.participate.notification.email.model.Email;
import de.vinado.app.participate.notification.email.model.EmailException;
import de.vinado.app.participate.notification.email.model.TemplatedEmailFactory;
import de.vinado.wicket.participate.common.ParticipateUtils;
import de.vinado.wicket.participate.configuration.ApplicationProperties;
import de.vinado.wicket.participate.model.Accommodation;
import de.vinado.wicket.participate.model.Event;
import de.vinado.wicket.participate.model.EventDetails;
import de.vinado.wicket.participate.model.InvitationStatus;
import de.vinado.wicket.participate.model.Participant;
import de.vinado.wicket.participate.model.Singer;
import de.vinado.wicket.participate.model.Terminable;
import de.vinado.wicket.participate.model.User;
import de.vinado.wicket.participate.model.dtos.EventDTO;
import de.vinado.wicket.participate.model.dtos.ParticipantDTO;
import jakarta.mail.internet.InternetAddress;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.wicket.util.string.Strings;
import org.springframework.context.annotation.Primary;
import org.springframework.format.Printer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.vinado.app.participate.notification.email.app.SendEmail.send;
import static de.vinado.app.participate.notification.email.model.Recipient.to;
import static de.vinado.wicket.participate.common.DateUtils.toLocalDate;
import static java.time.temporal.ChronoUnit.DAYS;

@Primary
@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl extends DataService implements EventService {

    private final PersonService personService;
    private final EmailService emailService;
    private final ApplicationProperties applicationProperties;
    private final TemplatedEmailFactory emailFactory;
    private final CalendarUrl calendarUrl;
    private final Printer<EventName> eventNamePrinter;

    @Override
    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Event createEvent(EventDTO dto, Locale locale) {
        if (Strings.isEmpty(dto.getName())) {
            dto.setName(ParticipateUtils.getGenericEventName(dto, locale));
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

    @Override
    public Event saveEvent(EventDTO dto, Locale locale) {
        Event loadedEvent = load(Event.class, dto.getEvent().getId());

        if (Strings.isEmpty(dto.getName())) {
            dto.setName(ParticipateUtils.getGenericEventName(dto, locale));
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

    @Override
    public void removeEvent(Event event) {
        Event loadedEvent = load(Event.class, event.getId());
        loadedEvent.setActive(false);
        save(loadedEvent);
    }

    @Override
    public Participant createParticipant(Event event, Singer singer) {
        Participant participant = new Participant(event, singer, generateEventToken(20), InvitationStatus.UNINVITED);
        return save(participant);
    }

    private String generateEventToken(int length) {
        String accessToken = RandomStringUtils.randomAlphanumeric(length);

        if (hasToken(accessToken)) {
            return generateEventToken(length);
        } else {
            return accessToken;
        }
    }

    @Override
    public Participant saveParticipant(ParticipantDTO dto) {
        Participant loadedParticipant = load(Participant.class, dto.getParticipant().getId());
        loadedParticipant.setInvitationStatus(dto.getInvitationStatus());
        loadedParticipant.setFromDate(dto.getFromDate());
        loadedParticipant.setToDate(dto.getToDate());
        loadedParticipant.setAccommodation(dto.getAccommodation());
        loadedParticipant.setCarSeatCount(dto.isCar() ? dto.getCarSeatCount() : -1);
        loadedParticipant.setComment(dto.getComment());
        return save(loadedParticipant);
    }

    @Override
    public Participant acceptEvent(ParticipantDTO dto) {
        dto.setInvitationStatus(InvitationStatus.ACCEPTED);
        return saveParticipant(dto);
    }

    @Override
    public Participant acceptEventTentatively(ParticipantDTO dto) {
        dto.setInvitationStatus(InvitationStatus.TENTATIVE);
        return saveParticipant(dto);
    }

    @Override
    public Participant declineEvent(ParticipantDTO dto) {
        dto.setInvitationStatus(InvitationStatus.DECLINED);
        dto.setFromDate(null);
        dto.setToDate(null);
        dto.setAccommodation(new Accommodation());
        dto.setCar(false);
        dto.setCarSeatCount((short) 0);
        return saveParticipant(dto);
    }

    @Override
    public boolean hasToken(String token) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<Participant> root = criteriaQuery.from(Participant.class);
        criteriaQuery.select(criteriaBuilder.count(root));
        criteriaQuery.where(criteriaBuilder.equal(root.get("token"), token));
        return 0 != entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    @Override
    public EventDetails getLatestEventDetails() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<EventDetails> criteriaQuery = criteriaBuilder.createQuery(EventDetails.class);
        Root<EventDetails> root = criteriaQuery.from(EventDetails.class);
        criteriaQuery.where(forUpcomingDate(criteriaBuilder, root), forActive(criteriaBuilder, root));
        try {
            return entityManager.createQuery(criteriaQuery).setMaxResults(1).getSingleResult();
        } catch (NoResultException e) {
            log.trace("Could not find any upcoming Event");
            return null;
        }
    }

    @Override
    public Event getLatestEvent() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> criteriaQuery = criteriaBuilder.createQuery(Event.class);
        Root<Event> root = criteriaQuery.from(Event.class);
        criteriaQuery.where(forUpcomingDate(criteriaBuilder, root), forActive(criteriaBuilder, root));
        criteriaQuery.orderBy(criteriaBuilder.asc(root.get("startDate")));
        try {
            return entityManager.createQuery(criteriaQuery).setMaxResults(1).getSingleResult();
        } catch (NoResultException e) {
            log.trace("Could not find any upcoming Event");
            return null;
        }
    }

    @Override
    public EventDetails getSuccessor(EventDetails eventDetails) {
        Date startDate = load(EventDetails.class, eventDetails.getId()).getStartDate();

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<EventDetails> criteriaQuery = criteriaBuilder.createQuery(EventDetails.class);
        Root<EventDetails> root = criteriaQuery.from(EventDetails.class);
        Predicate forStartDate = criteriaBuilder.greaterThan(root.get("startDate"), startDate);
        criteriaQuery.where(forUpcomingDate(criteriaBuilder, root), forStartDate);
        try {
            return entityManager.createQuery(criteriaQuery).setMaxResults(1).getSingleResult();
        } catch (NoResultException e) {
            log.trace("Could not find successor of Event /w id={}", eventDetails.getEvent().getId());
            return null;
        }
    }

    @Override
    public EventDetails getPredecessor(EventDetails eventDetails) {
        Date startDate = load(EventDetails.class, eventDetails.getId()).getStartDate();

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<EventDetails> criteriaQuery = criteriaBuilder.createQuery(EventDetails.class);
        Root<EventDetails> root = criteriaQuery.from(EventDetails.class);
        Predicate forStartDate = criteriaBuilder.lessThan(root.get("startDate"), startDate);
        criteriaQuery.where(forUpcomingDate(criteriaBuilder, root), forStartDate);
        criteriaQuery.orderBy(criteriaBuilder.desc(root.get("startDate")));
        try {
            return entityManager.createQuery(criteriaQuery).setMaxResults(1).getSingleResult();
        } catch (NoResultException e) {
            log.trace("Could not find predecessor of Event /w id={}", eventDetails.getEvent().getId());
            return null;
        }
    }

    @Override
    public List<Event> getUpcomingEvents() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> criteriaQuery = criteriaBuilder.createQuery(Event.class);
        Root<Event> root = criteriaQuery.from(Event.class);
        criteriaQuery.orderBy(criteriaBuilder.asc(root.<Event>get("startDate")));
        criteriaQuery.where(forUpcomingDate(criteriaBuilder, root));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    @Override
    public List<EventDetails> getUpcomingEventDetails() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<EventDetails> criteriaQuery = criteriaBuilder.createQuery(EventDetails.class);
        Root<EventDetails> root = criteriaQuery.from(EventDetails.class);
        Join<EventDetails, Event> eventJoin = root.join("event");
        criteriaQuery.where(forActive(criteriaBuilder, eventJoin), forUpcomingDate(criteriaBuilder, eventJoin));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    @Override
    public List<String> getEventTypes() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<String> criteriaQuery = criteriaBuilder.createQuery(String.class);
        Root<Event> root = criteriaQuery.from(Event.class);
        criteriaQuery.select(root.get("eventType"));
        List<String> resultList = entityManager.createQuery(criteriaQuery).getResultList();
        Map<String, Integer> frequency = resultList.stream()
            .collect(Collectors.toMap(Function.identity(), v -> 1, Integer::sum));
        List<String> eventTypes = new ArrayList<>(frequency.keySet());
        eventTypes.sort((a, b) -> frequency.get(b) - frequency.get(a));
        return eventTypes;
    }

    @Override
    public List<String> getLocationList() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<String> criteriaQuery = criteriaBuilder.createQuery(String.class);
        Root<Event> root = criteriaQuery.from(Event.class);
        criteriaQuery.select(root.get("location"));
        List<String> resultList = entityManager.createQuery(criteriaQuery).getResultList();
        Map<String, Integer> frequency = resultList.stream()
            .collect(Collectors.toMap(Function.identity(), v -> 1, Integer::sum));
        List<String> locations = new ArrayList<>(frequency.keySet());
        locations.sort((a, b) -> frequency.get(b) - frequency.get(a));
        return locations;
    }

    @Override
    public EventDetails getEventDetails(Event event) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<EventDetails> criteriaQuery = criteriaBuilder.createQuery(EventDetails.class);
        Root<EventDetails> root = criteriaQuery.from(EventDetails.class);
        criteriaQuery.where(criteriaBuilder.equal(root.get("event"), event));
        try {
            return entityManager.createQuery(criteriaQuery).getSingleResult();
        } catch (NoResultException e) {
            log.trace("Could not find Event Details for Event /w id={}", event.getId());
            return null;
        }
    }

    @Override
    public List<Participant> getParticipants(Event event) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Participant> criteriaQuery = criteriaBuilder.createQuery(Participant.class);
        Root<Participant> root = criteriaQuery.from(Participant.class);
        criteriaQuery.where(criteriaBuilder.equal(root.<Event>get("event"), event));
        criteriaQuery.orderBy(criteriaBuilder.asc(root.join("singer").get("lastName")));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    @Override
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

    @Override
    public List<Participant> getUninvitedParticipants(Event event) {
        return getParticipants(event, InvitationStatus.UNINVITED);
    }

    @Override
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

    @Override
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

    @Override
    public Participant getParticipant(String token) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Participant> criteriaQuery = criteriaBuilder.createQuery(Participant.class);
        Root<Participant> root = criteriaQuery.from(Participant.class);
        criteriaQuery.where(criteriaBuilder.equal(root.<String>get("token"), token));
        try {
            return entityManager.createQuery(criteriaQuery).getSingleResult();
        } catch (NoResultException e) {
            log.trace("Could not find Participant /w token={}", token);
            return null;
        }
    }

    @Override
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

    @Override
    public int inviteParticipants(List<Participant> participants, User organizer) {
        int count = 0;

        try {
            Map<Participant, Email> invitations = participants.stream()
                .map(this::createInvitation)
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));

            for (Entry<Participant, Email> invitation : invitations.entrySet()) {
                Participant participant = invitation.getKey();
                Email email = invitation.getValue();
                InternetAddress recipient = create(participant.getSinger());
                emailService.execute(send(email).individually(to(recipient)));
                count++;

                Participant loadedParticipant = load(Participant.class, participant.getId());
                loadedParticipant.setInvitationStatus(InvitationStatus.PENDING);
                save(loadedParticipant);
            }
        } catch (EmailException e) {
            log.error("Could not send invitation", e);
        }

        return count;
    }

    @SneakyThrows
    private static InternetAddress create(Singer singer) {
        return new InternetAddress(singer.getEmail(), singer.getDisplayName(), "UTF-8");
    }

    private Entry<Participant, Email> createInvitation(Participant participant) {
        Singer singer = participant.getSinger();
        Event event = participant.getEvent();
        int offset = 1 - applicationProperties.getDeadlineOffset();
        Date deadline = DateUtils.addDays(event.getStartDate(), offset);

        Map<String, Object> data = new HashMap<>();
        data.put("event", event);
        data.put("singer", singer);
        data.put("acceptLink", ParticipateUtils.generateInvitationLink(applicationProperties.getBaseUrl(), participant.getToken()));
        data.put("deadline", offset > 1 ? null : deadline);
        data.put("calendarUrl", calendarUrl.apply(event, Locale.getDefault()));

        String subject = eventNamePrinter.print(EventName.of(event), Locale.getDefault());
        Email email = emailFactory.create(subject, "inviteSinger-txt.ftl", "inviteSinger-html.ftl", data);
        return Map.entry(participant, email);
    }

    @Override
    public void inviteParticipant(Participant participant, User organizer) {
        inviteParticipants(Collections.singletonList(participant), organizer);
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

    @Override
    public Optional<EventDetails> findById(Long id) {
        TypedQuery<EventDetails> query = entityManager.createQuery("SELECT e FROM EventDetails e WHERE e.event.id = :id", EventDetails.class)
            .setParameter("id", id);
        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    private static Predicate forUpcomingDate(CriteriaBuilder criteriaBuilder, Path<? extends Terminable> eventPath) {
        return criteriaBuilder.greaterThanOrEqualTo(eventPath.get("endDate"), new Date());
    }
}
