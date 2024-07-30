package de.vinado.wicket.participate.services;

import de.vinado.wicket.participate.model.Event;
import de.vinado.wicket.participate.model.EventDetails;
import de.vinado.wicket.participate.model.InvitationStatus;
import de.vinado.wicket.participate.model.Participant;
import de.vinado.wicket.participate.model.Singer;
import de.vinado.wicket.participate.model.User;
import de.vinado.wicket.participate.model.dtos.EventDTO;
import de.vinado.wicket.participate.model.dtos.ParticipantDTO;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

public interface EventService {

    Event createEvent(EventDTO dto, Locale locale);

    Event saveEvent(EventDTO dto, Locale locale);

    void removeEvent(Event event);

    Participant createParticipant(Event event, Singer singer);

    Participant saveParticipant(ParticipantDTO dto);

    Participant acceptEvent(ParticipantDTO dto);

    Participant acceptEventTentatively(ParticipantDTO dto);

    Participant declineEvent(ParticipantDTO dto);

    boolean hasToken(String token);

    EventDetails getLatestEventDetails();

    Event getLatestEvent();

    EventDetails getSuccessor(EventDetails eventDetails);

    EventDetails getPredecessor(EventDetails eventDetails);

    List<Event> getUpcomingEvents();

    List<EventDetails> getUpcomingEventDetails();

    List<String> getEventTypes();

    List<String> getLocationList();

    EventDetails getEventDetails(Event event);

    List<Participant> getParticipants(Event event);

    default List<Participant> getParticipants(Event event, boolean invited) {
        return invited ? getInvitedParticipants(event) : getUninvitedParticipants(event);
    }

    List<Participant> getInvitedParticipants(Event event);

    List<Participant> getUninvitedParticipants(Event event);

    List<Participant> getParticipants(Event event, InvitationStatus invitationStatus);

    boolean hasParticipant(Event event);

    Participant getParticipant(String token);

    List<Participant> getParticipants(Singer singer);

    int inviteParticipants(List<Participant> participants, User organizer);

    void inviteParticipant(Participant participant, User organizer);

    boolean hasDeadlineExpired(Participant participant);

    Stream<EventDetails> listAll();

    Optional<EventDetails> findById(Long id);
}
