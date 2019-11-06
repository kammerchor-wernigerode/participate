package de.vinado.wicket.participate.services;

import de.vinado.wicket.participate.model.EventDetails;

import java.util.Date;
import java.util.Optional;

/**
 * Repository for managing event details.
 *
 * @author Vincent Nadoll
 */
public interface EventDetailsRepository extends EventBaseRepository<EventDetails> {

    /**
     * @return optional of the next event
     */
    Optional<EventDetails> findFirstBy();

    /**
     * @param startDate the event's start date for which to load the successor
     * @return optional of the next event after the given start date
     */
    Optional<EventDetails> findFirstByStartDateAfter(Date startDate);

    /**
     * @param startDate the event's start date for which to load the predecessor
     * @return optional of the previous event before the given start date
     */
    Optional<EventDetails> findFirstByStartDateBeforeOrderByStartDateDesc(Date startDate);
}
