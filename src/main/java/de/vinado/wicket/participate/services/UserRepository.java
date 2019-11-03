package de.vinado.wicket.participate.services;

import de.vinado.wicket.participate.model.Person;
import de.vinado.wicket.participate.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for managing users.
 *
 * @author Vincent Nadoll
 */
public interface UserRepository extends CrudRepository<User, Long> {

    /**
     * @return list of all active users
     */
    List<User> findAll();

    /**
     * Retrieves all users where the given username matches partially.
     *
     * @param username the username which have to match partially
     * @return list of users
     */
    List<User> findAllByUsernameLikeIgnoreCase(String username);

    /**
     * Retrieves a user by his username.
     *
     * @param username the username for which the user should be found
     * @return optional of the user by his username
     */
    Optional<User> findByUsername(String username);

    /**
     * Retrieves a user by his assigned person.
     *
     * @param person the assigned person for which the user should be found
     * @return optional of the user by his assigned person
     */
    Optional<User> findByPerson(Person person);

    /**
     * Retrieves a user by his login expression.
     *
     * @param login the login expression for which the user should be found
     * @return optional of the user for his login expression
     *
     * @see User for query
     */
    @Query(name = "User.findByLogin")
    Optional<User> findByLogin(String login);

    /**
     * Retrieves a user for his credentials.
     *
     * @param login    the login expression for which the user should be loaded
     * @param password the password for which the user should be loaded
     * @return optional of the authenticated user
     *
     * @see User for query
     */
    @Query(name = "User.authenticate")
    Optional<User> authenticate(String login, String password);

    /**
     * @param username the username for which the user should be found
     * @return {@code true} if the user exist; {@code false} otherwise
     */
    boolean existsByUsername(String username);

    /**
     * @param person the assigned person for which the user should be found
     * @return {@code true} if the user exist; {@code false} otherwise
     */
    boolean existsByPerson(Person person);
}
