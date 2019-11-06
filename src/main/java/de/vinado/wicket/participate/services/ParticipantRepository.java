package de.vinado.wicket.participate.services;

import de.vinado.wicket.participate.model.Event;
import de.vinado.wicket.participate.model.Participant;
import de.vinado.wicket.participate.model.Singer;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for managing event participants.
 *
 * @author Vincent Nadoll
 */
public interface ParticipantRepository extends CrudRepository<Participant, Long> {

    /**
     * Retrieves all event attendees ordered by singer's last name.
     *
     * @param event the event for which the participants should be fetched
     * @return list of event participants
     */
    List<Participant> findAllByEventOrderBySingerLastNameAsc(Event event);

    /**
     * Retrieves all participants for the given singer.
     *
     * @param singer the singer for which the participants should be fetched
     * @return list of participants
     */
    List<Participant> findAllBySinger(Singer singer);

    /**
     * Retrieves a participant for his token.
     *
     * @param token the token for which the participant should be fetched
     * @return optional of participant
     */
    Optional<Participant> findByToken(String token);

    /**
     * Retrieves the next upcoming event's participant for the given singer.
     *
     * @param singer the singer for which the participant should be fetched
     * @return optional of the next upcoming event's participant
     */
    Optional<Participant> findFirstBySingerOrderByEventStartDateAsc(Singer singer);

    /**
     * @param event the event for which the event should be found
     * @return {@code true} if a participant exists for the given event; {@code false} otherwise
     */
    boolean existsByEvent(Event event);

    /**
     * @param token the token for which the event should be found
     * @return {@code true} if a participants exists; {@code false} otherwise
     */
    boolean existsByToken(String token);
}
