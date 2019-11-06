package de.vinado.wicket.participate.services;

import de.vinado.wicket.participate.model.UserRecoveryToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * Repository for managing user recovery tokens.
 *
 * @author Vincent Nadoll
 */
public interface UserRecoveryTokenRepository extends CrudRepository<UserRecoveryToken, Long> {

    /**
     * Retrieves a user recovery token by its token.
     *
     * @param token the token for which the user recovery token should be found
     * @return optional of the user recovery token
     */
    Optional<UserRecoveryToken> findByToken(String token);

    /**
     * @param token the token for which the user recovery token should be found
     * @return {@code true} if the token exist; {@code false} otherwise
     */
    boolean existsByToken(String token);
}
