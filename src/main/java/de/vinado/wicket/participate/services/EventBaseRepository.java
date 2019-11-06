package de.vinado.wicket.participate.services;

import de.vinado.wicket.participate.model.Event;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Base repository for managing event related objects.
 *
 * @author Vincent Nadoll
 */
@NoRepositoryBean
public interface EventBaseRepository<T extends Event> extends CrudRepository<T, Long> {

    /**
     * @return lists all active events
     */
    List<T> findAll();

    /**
     * Retrieves all upcoming events ordered by their start date
     *
     * @return list of upcoming events
     */
    List<T> findAllByOrderByStartDateAsc();

    /**
     * @return optional of the next event
     */
    Optional<T> findFirstByOrderByStartDateAsc();

    /**
     * @return optional of the next event
     */
    Optional<T> findFirstBy();

    /**
     * @param startDate the event's start date for which to load the successor
     * @return optional of the next event after the given start date
     */
    Optional<T> findFirstByStartDateAfter(Date startDate);

    /**
     * @param startDate the event's start date for which to load the predecessor
     * @return optional of the previous event before the given start date
     */
    Optional<T> findFirstByStartDateBeforeOrderByStartDateDesc(Date startDate);
}
