package de.vinado.wicket.participate.service;

import de.vinado.wicket.participate.ParticipateApplication;
import de.vinado.wicket.participate.ParticipateSession;
import de.vinado.wicket.participate.common.ParticipateUtils;
import de.vinado.wicket.participate.configuration.ApplicationProperties;
import de.vinado.wicket.participate.data.Address;
import de.vinado.wicket.participate.data.AddressToEvent;
import de.vinado.wicket.participate.data.Event;
import de.vinado.wicket.participate.data.Group;
import de.vinado.wicket.participate.data.GroupToEvent;
import de.vinado.wicket.participate.data.InvitationStatus;
import de.vinado.wicket.participate.data.ListOfValue;
import de.vinado.wicket.participate.data.Member;
import de.vinado.wicket.participate.data.MemberToEvent;
import de.vinado.wicket.participate.data.Person;
import de.vinado.wicket.participate.data.Voice;
import de.vinado.wicket.participate.data.dto.EventDTO;
import de.vinado.wicket.participate.data.dto.MemberToEventDTO;
import de.vinado.wicket.participate.data.email.EmailAttachment;
import de.vinado.wicket.participate.data.email.MailData;
import de.vinado.wicket.participate.data.filter.DetailedMemberToEventFilter;
import de.vinado.wicket.participate.data.filter.EventFilter;
import de.vinado.wicket.participate.data.filter.MemberToEventFilter;
import de.vinado.wicket.participate.data.ical4j.SimpleDateProperty;
import de.vinado.wicket.participate.data.view.EventDetailsView;
import de.vinado.wicket.participate.data.view.EventView;
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

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
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
    private ListOfValueService listOfValueService;

    @Autowired
    private AddressService addressService;

    @Autowired
    private EmailService emailService;

    /**
     * {@link de.vinado.wicket.participate.service.DataService}
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
            + dto.getLocality()).replaceAll("[^A-Za-z0-9]", "").toUpperCase());

        if (Strings.isEmpty(dto.getName())) {
            dto.setName(ParticipateUtils.getGenericEventName(dto));
        }

        // Event
        Event event = new Event(dto.getIdentifier(), dto.getName(), dto.getEventType(), dto.getDescription(),
            dto.getStartDate(), dto.getEndDate());
        event = save(event);

        // Address to Event
        final Address address = save(new Address(dto.getLocality()));
        save(event.addAddressForObject(address));

        // Event to member
        for (Member member : personService.getGroupMemberList(save(new GroupToEvent(event, dto.getGroup())).getGroup())) {
            createMemberToEvent(event, member);
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
        final Address loadedAddress = getAddressToEvent(loadedEvent).getAddress();

        if (Strings.isEmpty(dto.getName())) {
            dto.setName(ParticipateUtils.getGenericEventName(dto));
        }

        loadedAddress.setLocality(dto.getLocality());
        addressService.saveAddress(loadedAddress);

        loadedEvent.setName(dto.getName());
        loadedEvent.setEventType(dto.getEventType());
        loadedEvent.setDescription(dto.getDescription());
        loadedEvent.setStartDate(dto.getStartDate());
        loadedEvent.setEndDate(dto.getEndDate());

        if (!clonedEvent.equals(loadedEvent)) {
            if (!ParticipateApplication.get().isInDevelopmentMode()) {
                // TODO Write notification template
                //inviteMembersToEvent(loadedEvent, getMemberToEventList(loadedEvent));
            } else {
                inviteMembersToEvent(loadedEvent, Collections.singletonList(getMemberToEvent(
                    ParticipateSession.get().getUser().getPerson().getEmail(), loadedEvent.getId())), false);
            }
        }

        return save(loadedEvent);
    }


    /**
     * Actually removes the {@link Event} and the {@link MemberToEvent} from the database.
     *
     * @param event {@link Event}
     */
    @Transactional
    public void removeEvent(final Event event) {
        final Event loadedEvent = load(Event.class, event.getId());

        /*remove(load(AddressToEvent.class, getAddressToEvent(loadedEvent).getId()));

        remove(getGroupToEvent(event));

        for (MemberToEvent memberToEvent : getMemberToEventList(loadedEvent)) {
            remove(load(MemberToEvent.class, memberToEvent.getId()));
        }
        remove(loadedEvent);*/

        loadedEvent.setActive(false);
        save(loadedEvent);
    }

    /**
     * Creates the mapping between {@link Event} and {@link Member}
     *
     * @param event  Event
     * @param member Member
     * @return Created mapping
     */
    @Transactional
    public MemberToEvent createMemberToEvent(final Event event, final Member member) {
        final MemberToEvent memberToEvent = new MemberToEvent(
            event,
            member,
            generateEventToken(20),
            (InvitationStatus) listOfValueService.getDefaultFromConfigurable(InvitationStatus.class));
        return save(memberToEvent);
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
    public MemberToEvent saveEventToMember(final MemberToEventDTO dto) {
        final MemberToEvent loadedMemberToEvent = load(MemberToEvent.class, dto.getMemberToEvent().getId());
        loadedMemberToEvent.setInvitationStatus(dto.getInvitationStatus());
        loadedMemberToEvent.setFromDate(dto.getFromDate());
        loadedMemberToEvent.setToDate(dto.getToDate());
        loadedMemberToEvent.setNeedsDinner(dto.isNeedsDinner());
        loadedMemberToEvent.setNeedsDinnerComment(dto.getNeedsDinnerComment());
        loadedMemberToEvent.setNeedsPlaceToSleep(dto.isNeedsPlaceToSleep());
        loadedMemberToEvent.setNeedsPlaceToSleepComment(dto.getNeedsPlaceToSleepComment());
        loadedMemberToEvent.setComment(dto.getComment());
        loadedMemberToEvent.setReviewed(dto.isReviewed());
        return save(loadedMemberToEvent);
    }

    @Transactional
    public MemberToEvent acceptEvent(final MemberToEventDTO dto) {
        dto.setInvitationStatus((InvitationStatus) listOfValueService.getConfigurable(InvitationStatus.class, InvitationStatus.ACCEPTED));
        dto.setReviewed(false);
        return saveEventToMember(dto);
    }

    @Transactional
    public MemberToEvent declineEvent(final MemberToEventDTO dto) {
        dto.setInvitationStatus((InvitationStatus) listOfValueService.getConfigurable(InvitationStatus.class, InvitationStatus.DECLINED));
        dto.setFromDate(null);
        dto.setToDate(null);
        dto.setNeedsDinner(false);
        dto.setNeedsPlaceToSleep(false);
        dto.setReviewed(false);
        return saveEventToMember(dto);
    }

    @Transactional
    public MemberToEvent declineEvent(final MemberToEvent memberToEvent) {
        return declineEvent(new MemberToEventDTO(memberToEvent));
    }

    public boolean hasEventToken(final String accessToken) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        final Root<MemberToEvent> root = criteriaQuery.from(MemberToEvent.class);
        criteriaQuery.select(criteriaBuilder.count(root));
        criteriaQuery.where(criteriaBuilder.equal(root.get("token"), accessToken));
        return 0 != entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    public boolean hasUpcomingEvents() {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        final Root<EventView> root = criteriaQuery.from(EventView.class);
        criteriaQuery.select(criteriaBuilder.count(root));
        return 0 != entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    public MemberToEvent getLatestMemberToEvent(final Member member) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<MemberToEvent> criteriaQuery = criteriaBuilder.createQuery(MemberToEvent.class);
        final Root<MemberToEvent> root = criteriaQuery.from(MemberToEvent.class);
        final Join<MemberToEvent, Event> eventJoin = root.join("event");
        final Predicate forMember = criteriaBuilder.equal(root.get("member"), member);
        final Predicate forDate = criteriaBuilder.greaterThanOrEqualTo(eventJoin.get("endDate"), new Date());
        criteriaQuery.where(forMember, forDate);
        criteriaQuery.orderBy(criteriaBuilder.asc(eventJoin.get("startDate")));
        try {
            return entityManager.createQuery(criteriaQuery).setMaxResults(1).getSingleResult();
        } catch (final NoResultException e) {
            LOGGER.info("Next MemberToEvent could not be found for member={}", member);
            return null;
        }
    }

    public EventView getLatestEventView() {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<EventView> criteriaQuery = criteriaBuilder.createQuery(EventView.class);
        final Root<EventView> root = criteriaQuery.from(EventView.class);
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

    public EventDetailsView getNextEventDetailsView(final Long id) {
        final Date startDate = load(EventDetailsView.class, id).getStartDate();
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<EventDetailsView> criteriaQuery = criteriaBuilder.createQuery(EventDetailsView.class);
        final Root<EventDetailsView> root = criteriaQuery.from(EventDetailsView.class);
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

    public EventDetailsView getPreviousEventDetailsView(final Long id) {
        final Date startDate = load(EventDetailsView.class, id).getStartDate();
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<EventDetailsView> criteriaQuery = criteriaBuilder.createQuery(EventDetailsView.class);
        final Root<EventDetailsView> root = criteriaQuery.from(EventDetailsView.class);
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

    public AddressToEvent getAddressToEvent(final Event event) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<AddressToEvent> criteriaQuery = criteriaBuilder.createQuery(AddressToEvent.class);
        final Root<AddressToEvent> root = criteriaQuery.from(AddressToEvent.class);
        criteriaQuery.where(criteriaBuilder.equal(root.get("event"), event));
        try {
            return entityManager.createQuery(criteriaQuery).getSingleResult();
        } catch (final NoResultException | NonUniqueResultException e) {
            LOGGER.warn("Zero or more than one addresses where found for event={}", event);
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

    public List<Event> getUpcomingEventList(final Group group) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Event> criteriaQuery = criteriaBuilder.createQuery(Event.class);
        final Root<GroupToEvent> root = criteriaQuery.from(GroupToEvent.class);
        final Join<GroupToEvent, Event> eventJoin = root.join("event");
        final Predicate forGroup = criteriaBuilder.equal(root.get("group"), group);
        final Predicate forEvent = criteriaBuilder.greaterThanOrEqualTo(eventJoin.get("endDate"), new Date());
        criteriaQuery.select(root.get("event"));
        criteriaQuery.where(forGroup, forEvent);
        criteriaQuery.orderBy(criteriaBuilder.asc(eventJoin.get("startDate")));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    public List<EventView> getUpcomingEventViewList() {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<EventView> criteriaQuery = criteriaBuilder.createQuery(EventView.class);
        final Root<EventView> root = criteriaQuery.from(EventView.class);
        final Join<EventView, Event> eventJoin = root.join("event");
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
        final Root<AddressToEvent> root = criteriaQuery.from(AddressToEvent.class);
        final Join<AddressToEvent, Address> addressJoin = root.join("address");
        criteriaQuery.select(addressJoin.get("locality"));
        criteriaQuery.groupBy(addressJoin.<String>get("locality"));
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

    public EventView getEventView(final Event event) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<EventView> criteriaQuery = criteriaBuilder.createQuery(EventView.class);
        final Root<EventView> root = criteriaQuery.from(EventView.class);
        criteriaQuery.where(criteriaBuilder.equal(root.get("event"), event));
        try {
            return entityManager.createQuery(criteriaQuery).getSingleResult();
        } catch (final NoResultException e) {
            LOGGER.warn("A view with event_id={} could not be found.", event.getId());
            return null;
        }
    }

    public EventDetailsView getViewEventDetails(final Event event) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<EventDetailsView> criteriaQuery = criteriaBuilder.createQuery(EventDetailsView.class);
        final Root<EventDetailsView> root = criteriaQuery.from(EventDetailsView.class);
        criteriaQuery.where(criteriaBuilder.equal(root.get("event"), event));
        try {
            return entityManager.createQuery(criteriaQuery).getSingleResult();
        } catch (final NoResultException e) {
            LOGGER.warn("A view with event_id={} could not be found.", event.getId());
            return null;
        }
    }

    /**
     * Returns the mapping between {@link Event Events} and {@link de.vinado.wicket.participate.data.Event Users} from
     * the database.
     *
     * @param event {@link Event}
     * @return List of {@link MemberToEvent}
     */
    public List<MemberToEvent> getMemberToEventList(final Event event) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<MemberToEvent> criteriaQuery = criteriaBuilder.createQuery(MemberToEvent.class);
        final Root<MemberToEvent> root = criteriaQuery.from(MemberToEvent.class);
        criteriaQuery.where(criteriaBuilder.equal(root.<Event>get("event"), event));
        try {
            return entityManager.createQuery(criteriaQuery).getResultList();
        } catch (Exception e) {
            LOGGER.warn("Mapping between {} and member could not be found.", event.getName());
            return new ArrayList<>();
        }
    }

    public List<Person> getPersonList(final Event event) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Person> criteriaQuery = criteriaBuilder.createQuery(Person.class);
        final Root<MemberToEvent> root = criteriaQuery.from(MemberToEvent.class);
        final Join<MemberToEvent, Member> memberJoin = root.join("member");
        criteriaQuery.select(memberJoin.get("person"));
        criteriaQuery.where(criteriaBuilder.equal(root.get("event"), event));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    public List<MemberToEvent> getMemberToEventList4Invited(final Event event, final boolean invited) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<MemberToEvent> criteriaQuery = criteriaBuilder.createQuery(MemberToEvent.class);
        final Root<MemberToEvent> root = criteriaQuery.from(MemberToEvent.class);
        final Predicate forEvent = criteriaBuilder.equal(root.<Event>get("event"), event);
        final Predicate forInvited = criteriaBuilder.equal(root.get("invited"), invited);
        criteriaQuery.where(forEvent, forInvited);
        try {
            return entityManager.createQuery(criteriaQuery).getResultList();
        } catch (Exception e) {
            LOGGER.warn("Mapping between {} and member could not be found.", event.getName());
            return new ArrayList<>();
        }
    }

    public boolean hasInvitedMemberToEvent(final Event event) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        final Root<MemberToEvent> root = criteriaQuery.from(MemberToEvent.class);
        final Predicate forEvent = criteriaBuilder.equal(root.get("event"), event);
        final Predicate forInvited = criteriaBuilder.equal(root.get("invited"), true);
        criteriaQuery.select(criteriaBuilder.count(root));
        criteriaQuery.where(forInvited, forEvent);
        return 0 != entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    public MemberToEvent getMemberToEvent(final Member member, final Event event) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<MemberToEvent> criteriaQuery = criteriaBuilder.createQuery(MemberToEvent.class);
        final Root<MemberToEvent> root = criteriaQuery.from(MemberToEvent.class);
        final Predicate forEvent = criteriaBuilder.equal(root.<Event>get("event"), event);
        final Predicate forMember = criteriaBuilder.equal(root.<Member>get("member"), member);
        criteriaQuery.where(forEvent, forMember);
        try {
            return entityManager.createQuery(criteriaQuery).getSingleResult();
        } catch (final NoResultException e) {
            LOGGER.warn("Mapping between {} and {} does not exist.", event.getName(), member.getPerson().getDisplayName());
            return null;
        }
    }

    public MemberToEvent getMemberToEvent(final String email, final Long eventId) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<MemberToEvent> criteriaQuery = criteriaBuilder.createQuery(MemberToEvent.class);
        final Root<MemberToEvent> root = criteriaQuery.from(MemberToEvent.class);
        final Join<MemberToEvent, Event> eventJoin = root.join("event");
        final Join<MemberToEvent, Member> memberJoin = root.join("member");
        final Join<Member, Person> personJoin = memberJoin.join("person");
        final Predicate forEvent = criteriaBuilder.equal(eventJoin.get("id"), eventId);
        final Predicate forEmail = criteriaBuilder.equal(personJoin.get("email"), email);
        criteriaQuery.where(forEvent, forEmail);
        try {
            return entityManager.createQuery(criteriaQuery).getSingleResult();
        } catch (final NoResultException e) {
            LOGGER.warn("Mapping between Event with id={} and {} could not be found.", eventId, email);
            return null;
        }
    }

    public MemberToEvent getMemberToEvent(final String token) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<MemberToEvent> criteriaQuery = criteriaBuilder.createQuery(MemberToEvent.class);
        final Root<MemberToEvent> root = criteriaQuery.from(MemberToEvent.class);
        criteriaQuery.where(criteriaBuilder.equal(root.<String>get("token"), token));
        try {
            return entityManager.createQuery(criteriaQuery).getSingleResult();
        } catch (final NoResultException e) {
            LOGGER.warn("An entry for \"{}\" could not be found.", token);
            return null;
        }
    }

    public List<MemberToEvent> getMemberToEventList(final Member member) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<MemberToEvent> criteriaQuery = criteriaBuilder.createQuery(MemberToEvent.class);
        final Root<MemberToEvent> root = criteriaQuery.from(MemberToEvent.class);
        final Predicate forMember = criteriaBuilder.equal(root.get("member"), member);
        final Join<MemberToEvent, Event> eventJoin = root.join("event");
        final Predicate forEndDate = criteriaBuilder.greaterThanOrEqualTo(eventJoin.get("endDate"), new Date());
        final Predicate forActive = criteriaBuilder.equal(eventJoin.get("active"), true);
        criteriaQuery.where(forMember, forActive, forEndDate);
        criteriaQuery.orderBy(criteriaBuilder.asc(eventJoin.get("startDate")));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    public List<MemberToEvent> getEventToMember4PendingStatus(final Event event) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<MemberToEvent> criteriaQuery = criteriaBuilder.createQuery(MemberToEvent.class);
        final Root<MemberToEvent> root = criteriaQuery.from(MemberToEvent.class);
        final Predicate forEvent = criteriaBuilder.equal(root.<Event>get("event"), event);
        final Join<MemberToEvent, ListOfValue> lovJoin = root.join("invitationStatus");
        final Predicate forPending = criteriaBuilder.equal(lovJoin.get("identifier"), "PENDING");
        criteriaQuery.where(forEvent, forPending);
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    public List<Member> getMemberWithPendingStatus4Event(final Event event) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Member> criteriaQuery = criteriaBuilder.createQuery(Member.class);
        final Root<MemberToEvent> root = criteriaQuery.from(MemberToEvent.class);
        final Predicate forEvent = criteriaBuilder.equal(root.<Event>get("event"), event);
        final Join<MemberToEvent, Member> memberJoin = root.join("member");
        final Join<MemberToEvent, ListOfValue> lovJoin = root.join("invitationStatus");
        final Predicate forPending = criteriaBuilder.equal(lovJoin.get("identifier"), "PENDING");
        criteriaQuery.select(memberJoin);
        criteriaQuery.where(forEvent, forPending);
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    public String getToken(final Member member, final Event event) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<String> criteriaQuery = criteriaBuilder.createQuery(String.class);
        final Root<MemberToEvent> root = criteriaQuery.from(MemberToEvent.class);
        final Predicate forMember = criteriaBuilder.equal(root.get("member"), member);
        final Predicate forEvent = criteriaBuilder.equal(root.get("event"), event);
        criteriaQuery.select(root.get("token"));
        criteriaQuery.where(forMember, forEvent);
        try {
            return entityManager.createQuery(criteriaQuery).getSingleResult();
        } catch (final NoResultException e) {
            LOGGER.info("Token could not be found for member={} and event={}", member, event);
            return null;
        }
    }

    public List<Event> getEventList(final Group group) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Event> criteriaQuery = criteriaBuilder.createQuery(Event.class);
        final Root<GroupToEvent> root = criteriaQuery.from(GroupToEvent.class);
        criteriaQuery.select(root.get("event"));
        criteriaQuery.where(criteriaBuilder.equal(root.get("group"), group));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    public Group getGroup(final Event event) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Group> criteriaQuery = criteriaBuilder.createQuery(Group.class);
        final Root<GroupToEvent> root = criteriaQuery.from(GroupToEvent.class);
        criteriaQuery.select(root.get("group"));
        criteriaQuery.where(criteriaBuilder.equal(root.get("event"), event));
        try {
            return entityManager.createQuery(criteriaQuery).getSingleResult();
        } catch (final NoResultException e) {
            LOGGER.info("Group for {} could not be found.", event.getName());
            return null;
        }
    }

    public GroupToEvent getGroupToEvent(final Event event) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<GroupToEvent> criteriaQuery = criteriaBuilder.createQuery(GroupToEvent.class);
        final Root<GroupToEvent> root = criteriaQuery.from(GroupToEvent.class);
        criteriaQuery.where(criteriaBuilder.equal(root.get("event"), event));
        try {
            return entityManager.createQuery(criteriaQuery).getSingleResult();
        } catch (final NoResultException e) {
            LOGGER.info("Mapping could not be found for: {}", event);
            return null;
        }
    }

    public boolean hasMemberToEvent(final Member member, final Event event) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        final Root<MemberToEvent> root = criteriaQuery.from(MemberToEvent.class);
        final Predicate forMember = criteriaBuilder.equal(root.get("member"), member);
        final Predicate forEvent = criteriaBuilder.equal(root.get("event"), event);
        criteriaQuery.select(criteriaBuilder.count(root));
        criteriaQuery.where(forMember, forEvent);
        return 0 != entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    /**
     * Invites all Members from memberToEventList to {@link de.vinado.wicket.participate.data.Event}
     *
     * @param event             {@link de.vinado.wicket.participate.data.Event}
     * @param memberToEventList List of {@link de.vinado.wicket.participate.data.MemberToEvent} to invite
     * @return Amount of sent emails
     */
    @Transactional
    public int inviteMembersToEvent(final Event event, final List<MemberToEvent> memberToEventList, final boolean reminder) {
        int count = 0;
        final ApplicationProperties properties = ParticipateApplication.get().getApplicationProperties();
        final List<MimeMessagePreparator> preparators = new ArrayList<>();
        final Address address = getAddressToEvent(event).getAddress();

        for (MemberToEvent memberToEvent : memberToEventList) {
            final Member member = memberToEvent.getMember();

            final MailData mailData = new MailData(properties.getMail().getSender(), member.getPerson().getEmail(),
                event.getName()) {
                @Override
                public Map<String, Object> getData() {
                    final Map<String, Object> mailData = super.getData();
                    mailData.put("event", event);
                    mailData.put("location", address);
                    mailData.put("member", member);
                    mailData.put("acceptLink", ParticipateUtils.generateInvitationLink(memberToEvent.getToken()));
                    return mailData;
                }
            };
            if (!reminder) {
                mailData.setAttachment(newEventAttachment(event, address.getLocality()));
            }
            // TODO Template erstellen
            preparators.add(emailService.getMimeMessagePreparator(mailData, memberToEvent.isInvited() ? "fm-eventReminder.ftl" : "fm-eventInvite.ftl"));

            final MemberToEvent loadedMemberToEvent = load(MemberToEvent.class, memberToEvent.getId());
            loadedMemberToEvent.setInvited(true);
            save(loadedMemberToEvent);

            count++;
        }
        emailService.sendMail(preparators.toArray(new MimeMessagePreparator[preparators.size()]));

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

    public List<EventView> getFilteredEventList(final EventFilter eventFilter) {
        if (null == eventFilter) {
            return getUpcomingEventViewList();
        }

        if (eventFilter.isShowAll()) {
            return getAll(EventView.class);
        }

        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<EventView> criteriaQuery = criteriaBuilder.createQuery(EventView.class);
        final Root<EventView> root = criteriaQuery.from(EventView.class);

        final List<Predicate> orPredicates = new ArrayList<>();
        final List<Predicate> andPredicates = new ArrayList<>();

        final String searchTerm = eventFilter.getSearchTerm();
        if (!Strings.isEmpty(eventFilter.getSearchTerm())) {
            orPredicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),
                "%" + searchTerm.toLowerCase() + "%"));
            orPredicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("eventType")),
                "%" + searchTerm.toLowerCase() + "%"));
            orPredicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("cast")),
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

    public List<MemberToEvent> getFilteredEventToMemberList(final Event event, final MemberToEventFilter filter) {
        if (null == filter) {
            return getMemberToEventList(event);
        }

        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<MemberToEvent> criteriaQuery = criteriaBuilder.createQuery(MemberToEvent.class);
        final Root<MemberToEvent> root = criteriaQuery.from(MemberToEvent.class);
        final Join<MemberToEvent, Member> memberJoin = root.join("member");
        final Join<Member, Person> personJoin = memberJoin.join("person");

        final List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.equal(root.<Event>get("event"), event));

        final String searchTerm = filter.getSearchTerm();
        if (!Strings.isEmpty(searchTerm))
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(personJoin.get("searchName")), "%" + searchTerm + "%"));

        final Voice voice = filter.getVoice();
        if (null != voice)
            predicates.add(criteriaBuilder.equal(memberJoin.get("voice"), voice));

        final InvitationStatus invitationStatus = filter.getInvitationStatus();
        if (null != invitationStatus)
            predicates.add(criteriaBuilder.equal(root.get("invitationStatus"), invitationStatus));

        if (filter.isNotInvited()) {
            predicates.add(criteriaBuilder.equal(root.get("invited"), false));
        }

        criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()]));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    public List<MemberToEvent> getDetailedFilteredMemberToEventList(final Event event, final DetailedMemberToEventFilter filter) {
        if (null == filter) {
            return getMemberToEventList(event);
        }

        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<MemberToEvent> criteriaQuery = criteriaBuilder.createQuery(MemberToEvent.class);
        final Root<MemberToEvent> root = criteriaQuery.from(MemberToEvent.class);
        final Join<MemberToEvent, Member> memberJoin = root.join("member");
        final Join<Member, Person> personJoin = memberJoin.join("person");

        final List<Predicate> orPredicates = new ArrayList<>();
        final List<Predicate> andPredicates = new ArrayList<>();
        andPredicates.add(criteriaBuilder.equal(root.<Event>get("event"), event));

        final String name = filter.getName();
        if (!Strings.isEmpty(name))
            andPredicates.add(criteriaBuilder.like(criteriaBuilder.lower(personJoin.get("searchName")), "%" + name + "%"));

        final String comment = filter.getComment();
        if (!Strings.isEmpty(comment)) {
            orPredicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("needsDinnerComment")), "%" + comment + "%"));
            orPredicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("needsPlaceToSleepComment")), "%" + comment + "%"));
            orPredicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("comment")), "%" + comment + "%"));
        }

        final Voice voice = filter.getVoice();
        if (null != voice)
            andPredicates.add(criteriaBuilder.equal(memberJoin.get("voice"), voice));

        final InvitationStatus invitationStatus = filter.getInvitationStatus();
        if (null != invitationStatus)
            andPredicates.add(criteriaBuilder.equal(root.get("invitationStatus"), invitationStatus));

        final Date fromDate = filter.getFromDate();
        if (null != fromDate)
            andPredicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("fromDate"), fromDate));

        final Date toDate = filter.getToDate();
        if (null != toDate)
            andPredicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("toDate"), toDate));

        if (filter.isNeedsPlaceToSleep())
            andPredicates.add(criteriaBuilder.equal(root.get("needsPlaceToSleep"), true));

        if (filter.isNeedsDinner())
            andPredicates.add(criteriaBuilder.equal(root.get("needsDinner"), true));

        if (filter.isNotInvited())
            andPredicates.add(criteriaBuilder.equal(root.get("invited"), false));

        if (!andPredicates.isEmpty() && orPredicates.isEmpty()) {
            criteriaQuery.where(andPredicates.toArray(new Predicate[andPredicates.size()]));
        } else if (andPredicates.isEmpty() && !orPredicates.isEmpty()) {
            criteriaQuery.where(criteriaBuilder.or(orPredicates.toArray(new Predicate[orPredicates.size()])));
        } else {
            final Predicate and = criteriaBuilder.and(andPredicates.toArray(new Predicate[andPredicates.size()]));
            final Predicate or = criteriaBuilder.or(orPredicates.toArray(new Predicate[orPredicates.size()]));
            criteriaQuery.where(and, or);
        }

        return entityManager.createQuery(criteriaQuery).getResultList();
    }
}
