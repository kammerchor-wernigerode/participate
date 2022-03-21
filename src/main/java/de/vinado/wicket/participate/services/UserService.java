package de.vinado.wicket.participate.services;

import de.vinado.wicket.participate.model.Person;
import de.vinado.wicket.participate.model.User;
import de.vinado.wicket.participate.model.UserRecoveryToken;
import de.vinado.wicket.participate.model.dtos.AddUserDTO;

import java.util.List;

/**
 * Provides interaction with the database. This service takes care of {@link User} and user related objects.
 *
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public interface UserService {

    /**
     * Creates a new {@link User}.
     *
     * @param dto {@link AddUserDTO}
     * @return Saved {@link User}
     */
    User createUser(AddUserDTO dto);

    /**
     * Saves an existing {@link User}.
     *
     * @param dto {@link AddUserDTO}
     * @return Saved {@link User}
     */
    User saveUser(AddUserDTO dto);

    /**
     * Assigns a {@link Person} to an {@link User}. If {@link AddUserDTO#person} is null a new {@link Person} will be
     * created. If {@link Person#email} is already in user the {@link Person} will be used instead of creating a new
     * one. User must not be null.
     *
     * @param dto {@link AddUserDTO}
     * @return Saved {@link User}
     */
    User assignPerson(AddUserDTO dto);

    /**
     * Fetches all {@link User}s.
     *
     * @return List of {@link User}s
     */
    List<User> getUsers();

    /**
     * Fetches all {@link User}s for the filter term.
     *
     * @param term Filter term
     * @return List of filtered {@link User}s
     */
    List<User> findUsers(String term);

    /**
     * Fetches the {@link User} by its id.
     *
     * @param id {@link User#id}
     * @return the {@link User} by its id
     */
    User getUser(Long id);

    /**
     * Fetches an {@link User} for {@link User#username}.
     *
     * @param username {@link User#username}
     * @return {@link User} for {@link User#username}
     */
    User getUser(String username);

    /**
     * Fetches an {@link User} for {@link Person}.
     *
     * @param person {@link Person}
     * @return {@link User} for {@link Person}
     */
    User getUser(Person person);

    /**
     * Fetches an {@link User} for {@link User#username} or {@link Person#email} and the plaintext password from the
     * login form. This method is used to authenticate an User.
     *
     * @param usernameOrEmail {@link User#username} or {@link Person#email}
     * @param plainPassword   {@link User#passwordSha256} in plain text
     * @return {@link User}, if the credential are correct
     */
    User getUser(String usernameOrEmail, String plainPassword);

    /**
     * Return whether the {@link User} exists for {@link User#username}.
     *
     * @param username {@link User#username}
     * @return Whether the {@link User} exists for {@link User#username}
     */
    boolean hasUser(String username);

    /**
     * Return whether the {@link User} exists for {@link User#person}.
     *
     * @param person {@link Person}
     * @return Whether the {@link User} exists for {@link User#person}
     */
    boolean hasUser(Person person);

    /**
     * Return whether the {@link User} exists for {@link UserRecoveryToken#token}.
     *
     * @param token {@link UserRecoveryToken#token}
     * @return Whether the {@link User} exists for {@link UserRecoveryToken#token}
     */
    boolean hasUserRecoveryToken(String token);

    /**
     * Sends an email to the {@link User} of {@link User#username} or {@link Person#email} with an password reset link.
     *
     * @param usernameOrEmail {@link User#username} or {@link Person#email}
     * @param initial         Whether the invitation is a common password reset or an initial one.
     * @return Whether the email has been sent.
     */
    boolean startPasswordReset(String usernameOrEmail, boolean initial);

    /**
     * Saves the {@link User} with a new {@link User#passwordSha256} and sends an email when the password reset is
     * finished. The {@link UserRecoveryToken} will be removed from the database.
     *
     * @param recoveryToken    {@link UserRecoveryToken#token} from the email link
     * @param newPlainPassword {@link User#passwordSha256} in plaintext
     * @return Whether the email has benn sent successfully
     */
    boolean finishPasswordReset(String recoveryToken, String newPlainPassword);

    /**
     * {@inheritDoc}
     */
    List<User> getAll();
}
