package de.vinado.wicket.participate.services;

import de.vinado.wicket.participate.model.Event;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

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
}
