package de.vinado.wicket.participate.services;

import de.vinado.wicket.participate.model.Person;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;
import java.util.Optional;

/**
 * Base repository for managing person related entities.
 *
 * @author Vincent Nadoll
 */
@NoRepositoryBean
public interface PersonBaseRepository<T extends Person> extends CrudRepository<T, Long> {

    /**
     * @return list of all persons
     */
    List<T> findAll();

    /**
     * Retrieves all persons where the given {@code searchName} matches partially.
     *
     * @param searchName the search name which have to match partially
     * @return list of persons
     */
    List<T> findAllBySearchNameLikeIgnoreCase(String searchName);

    /**
     * Retrieves a person by its email.
     *
     * @param email the email address for which the person should be found
     * @return optional of the person by its email address
     */
    Optional<T> findByEmail(String email);

    /**
     * @param email the email address for which the person should be found
     * @return {@code true} if the person exist; {@code false} otherwise
     */
    boolean existsByEmail(String email);
}
