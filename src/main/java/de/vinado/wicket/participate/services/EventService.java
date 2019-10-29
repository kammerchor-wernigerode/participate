package de.vinado.wicket.participate.services;

import de.vinado.wicket.participate.model.Event;
import de.vinado.wicket.participate.model.EventDetails;
import de.vinado.wicket.participate.model.InvitationStatus;
import de.vinado.wicket.participate.model.Participant;
import de.vinado.wicket.participate.model.Person;
import de.vinado.wicket.participate.model.Singer;
import de.vinado.wicket.participate.model.User;
import de.vinado.wicket.participate.model.dtos.EventDTO;
import de.vinado.wicket.participate.model.dtos.ParticipantDTO;
import de.vinado.wicket.participate.model.filters.DetailedParticipantFilter;
import de.vinado.wicket.participate.model.filters.EventFilter;
import de.vinado.wicket.participate.model.filters.ParticipantFilter;

import java.util.List;

/**
 * The service takes care of {@link Event} and Event related objects.
 *
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public interface EventService {

    /**
     * Creates a new {@link de.vinado.wicket.participate.model.Event}.
     *
     * @param dto {@link EventDTO}
     * @return Saved {@link de.vinado.wicket.participate.model.Event}
     */
    Event createEvent(EventDTO dto);

    /**
     * Saves an existing {@link de.vinado.wicket.participate.model.Event}.
     *
     * @param dto {@link EventDTO}
     * @return Saved {@link de.vinado.wicket.participate.model.Event}
     */
    Event saveEvent(EventDTO dto);

    /**
     * Sets the {@link Event} to inactive.
     *
     * @param event {@link Event}
     */
    void removeEvent(Event event);

    /**
     * Creates a new {@link Participant}.
     *
     * @param event  {@link Event}
     * @param singer {@link Singer}
     * @return Saved {@link Participant}
     */
    Participant createParticipant(Event event, Singer singer);

    /**
     * Saves an existing {@link Participant}.
     *
     * @param dto {@link ParticipantDTO}
     * @return Saved {@link Participant}
     */
    Participant saveParticipant(ParticipantDTO dto);

    /**
     * Changes the {@link InvitationStatus} to {@link InvitationStatus#ACCEPTED}.
     *
     * @param dto {@link ParticipantDTO}
     * @return Saved {@link Participant}
     */
    Participant acceptEvent(ParticipantDTO dto);

    /**
     * Changes the {@link InvitationStatus} to {@link InvitationStatus#DECLINED} and resets the {@link Participant} to
     * default.
     *
     * @param dto {@link ParticipantDTO}
     * @return Saved {@link Participant}
     */
    Participant declineEvent(ParticipantDTO dto);

    /**
     * Returns whether the {@link Participant#token} exists.
     *
     * @param token {@link Participant#token}
     * @return Whether the {@link Participant#token} exists.
     */
    boolean hasToken(String token);

    /**
     * Returns whether upcoming {@link Event}s exits.
     *
     * @return Whether upcoming {@link Event}s exits.
     */
    boolean hasUpcomingEvents();

    /**
     * Fetches the {@link Participant} of the latest {@link Event} for the given {@link Singer}.
     *
     * @param singer {@link Singer}
     * @return {@link Participant} of its latest {@link Event}
     */
    Participant getLatestParticipant(Singer singer);

    /**
     * Fetches the latest {@link EventDetails}.
     *
     * @return Latest {@link EventDetails}
     */
    EventDetails getLatestEventDetails();

    /**
     * Fetches the latest {@link Event}.
     *
     * @return Latest {@link Event}
     */
    Event getLatestEvent();

    /**
     * Fetches the succeeding of {@link EventDetails}.
     *
     * @param eventDetails {@link EventDetails}
     * @return Next {@link EventDetails}
     */
    EventDetails getSuccessor(EventDetails eventDetails);

    /**
     * Fetches the preceding of {@link EventDetails}.
     *
     * @param eventDetails {@link EventDetails}
     * @return Previous {@link EventDetails}
     */
    EventDetails getPredecessor(EventDetails eventDetails);

    /**
     * Fetches all upcoming {@link Event}s, sorted by {@link Event#startDate}.
     *
     * @return Upcoming list {@link Event}s
     */
    List<Event> getUpcomingEvents();

    /**
     * Fetches all upcoming {@link EventDetails}
     *
     * @return Upcoming list {@link EventDetails}
     */
    List<EventDetails> getUpcomingEventDetails();

    /**
     * Fetches all grouped {@link Event#eventType}s.
     *
     * @return List of grouped {@link Event#eventType}s
     */
    List<String> getEventTypes();

    /**
     * Fetches all grouped {@link Event#location}s.
     *
     * @return List of grouped {@link Event#location}s
     */
    List<String> getLocationList();

    /**
     * Fetches all upcoming {@link Event}s with an start offset sorted by {@link Event#startDate}.
     *
     * @param offset Offset
     * @return List of {@link Event Events}.
     */
    List<Event> getUpcomingEvents(int offset);

    /**
     * Fetches an {@link EventDetails} for an {@link Event}.
     *
     * @param event {@link Event}
     * @return {@link EventDetails} for {@link Event}
     */
    EventDetails getEventDetails(Event event);

    /**
     * Fetches all {@link Participant}s where the {@link Event} is present. The result is ordered by
     * {@link Person#lastName}.
     *
     * @param event The {@link Event} to filter for.
     * @return The list of ordered {@link Participant}.
     */
    List<Participant> getParticipants(Event event);

    /**
     * Fetches all {@link Participant} where the {@link Event} is present and invited. The
     * result is ordered by {@link Person#lastName}.
     *
     * @param event   The {@link Event} to filter for.
     * @param invited Whether the {@link Participant} is not invited.
     * @return The list of ordered {@link Participant}.
     */
    default List<Participant> getParticipants(final Event event, final boolean invited) {
        return invited ? getInvitedParticipants(event) : getUninvitedParticipants(event);
    }

    /**
     * Fetches all invited {@link Participant}s of the given {@link Event} ordered by {@link Participant#singer}s
     * {@link Singer#lastName}.
     *
     * @param event {@link Event}
     * @return List of invited {@link Participant}s
     */
    List<Participant> getInvitedParticipants(Event event);

    /**
     * Fetches all {@link InvitationStatus#UNINVITED} {@link Participant}s of the given {@link Event} ordered by
     * {@link Participant#singer}s {@link Singer#lastName}.
     *
     * @param event {@link Event}
     * @return List of {@link InvitationStatus#UNINVITED} {@link Participant}s
     */
    List<Participant> getUninvitedParticipants(Event event);

    /**
     * Fetches all {@link Participant}s for {@link InvitationStatus} of the given {@link Event}, ordered by
     * {@link Participant#singer}s {@link Singer#lastName}.
     *
     * @param event            {@link Event}
     * @param invitationStatus {@link InvitationStatus}
     * @return List of {@link Participant}s
     */
    List<Participant> getParticipants(Event event, InvitationStatus invitationStatus);

    /**
     * Returns whether any {@link Participant} exists for the given {@link Event}.
     *
     * @param event {@link Event}
     * @return Whether any {@link Participant} exists for the given {@link Event}
     */
    boolean hasParticipant(Event event);

    /**
     * Fetches a {@link Participant} for {@link Event} and {@link Singer}.
     *
     * @param singer {@link Singer}
     * @param event  {@link Event}
     * @return {@link Participant} for {@link Event} and {@link Singer}
     */
    Participant getParticipant(Singer singer, Event event);

    /**
     * Fetches a {@link Participant} for {@link Singer#email} and {@link Event#id}.
     *
     * @param email   {@link Singer#email}
     * @param eventId {@link Event#id}
     * @return {@link Participant} for {@link Singer#email} and {@link Event#id}.
     */
    Participant getParticipant(String email, Long eventId);

    /**
     * Fetches a {@link Participant} for its {@link Participant#token}.
     *
     * @param token {@link Participant#token}
     * @return {@link Participant} for its {@link Participant#token}
     */
    Participant getParticipant(String token);

    /**
     * Fetches all {@link Participant}s where the {@link Singer} is present, is {@link Singer#active} and the
     * {@link Event#endDate} is greater than today. The result is ordered by {@link Event#startDate} and
     * {@link Person#lastName}.
     *
     * @param singer {@link Singer} to filter for.
     * @return List of ordered {@link Participant}s.
     */
    List<Participant> getParticipants(Singer singer);

    /**
     * Fetches all {@link Participant}s where the {@link Event} is present and the {@link Participant#invitationStatus}
     * equals {@link InvitationStatus#PENDING}. The result is ordered by {@link Person#lastName}.
     *
     * @param event {@link Event} to filter for.
     * @return List of ordered {@link Participant}s.
     */
    List<Participant> getPendingParticipants(Event event);

    /**
     * Fetches the {@link Participant#token} for its {@link Singer} and {@link Event}.
     *
     * @param singer {@link Singer}
     * @param event  {@link Event}
     * @return {@link Participant#token} for its {@link Singer} and {@link Event}
     */
    String getToken(Singer singer, Event event);

    /**
     * Returns whether any {@link Participant} exists for {@link Participant#singer} and {@link Participant#event}.
     *
     * @param singer {@link Singer}
     * @param event  {@link Event}
     * @return Whether any {@link Participant} exists for {@link Participant#singer} and {@link Participant#event}
     */
    boolean hasParticipant(Singer singer, Event event);

    /**
     * Sends an invitation to all participating {@link Singer}s of an {@link Event}.
     *
     * @param participants List of {@link Participant}s to invite
     * @param organizer    the organizer's email address
     * @return Amount of sent emails
     */
    int inviteParticipants(List<Participant> participants, User organizer);

    /**
     * Sends an invitation to the given {@link Participant}
     *
     * @param participant {@link Participant}
     * @param organizer   the organizer's email address
     */
    void inviteParticipant(Participant participant, User organizer);

    /**
     * Fetches all {@link EventDetails} that matches the {@link EventFilter}.
     *
     * @param eventFilter {@link EventFilter}
     * @return List of filtered {@link EventDetails}
     */
    List<EventDetails> getFilteredEventDetails(EventFilter eventFilter);

    /**
     * Fetches all {@link Participant} where the {@link Event} is present. The result is filtered by
     * {@link ParticipantFilter} and ordered by {@link Person#lastName}.
     *
     * @param event  The {@link Event} to filter for.
     * @param filter The filter criteria.
     * @return An filtered and ordered list of {@link Participant}.
     */
    List<Participant> getFilteredParticipants(Event event, ParticipantFilter filter);

    /**
     * A more in detail filter that fetches all {@link Participant} where the {@link Event} is present. The result
     * is filtered by {@link DetailedParticipantFilter} and ordered by {@link Person#lastName}.
     *
     * @param event  The {@link Event} to filter for.
     * @param filter The filter criteria.
     * @return An filtered and ordered list of {@link Participant}.
     */
    List<Participant> getDetailedFilteredParticipants(Event event, DetailedParticipantFilter filter);

    /**
     * @param participant the participant on which to determine whether the deadline has passed
     * @return {@code true} if the the given participant missed the deadline; {@code false} otherwise
     */
    boolean afterDeadline(Participant participant);
}
