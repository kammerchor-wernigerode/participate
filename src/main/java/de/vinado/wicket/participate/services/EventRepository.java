package de.vinado.wicket.participate.services;

import de.vinado.wicket.participate.model.Event;

import java.util.List;
import java.util.Optional;

/**
 * Repository for managing events.
 *
 * @author Vincent Nadoll
 */
public interface EventRepository extends EventBaseRepository<Event> {

    /**
     * Retrieves all upcoming events ordered by their start date
     *
     * @return list of upcoming events
     */
    List<Event> findAllByOrderByStartDateAsc();

    /**
     * @return optional of the next event
     */
    Optional<Event> findFirstByOrderByStartDateAsc();
}
