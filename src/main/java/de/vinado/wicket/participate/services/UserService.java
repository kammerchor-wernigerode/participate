package de.vinado.wicket.participate.services;

import de.vinado.wicket.participate.ParticipateApplication;
import de.vinado.wicket.participate.configuration.ApplicationProperties;
import de.vinado.wicket.participate.email.Email;
import de.vinado.wicket.participate.email.service.EmailService;
import de.vinado.wicket.participate.model.Person;
import de.vinado.wicket.participate.model.User;
import de.vinado.wicket.participate.model.UserRecoveryToken;
import de.vinado.wicket.participate.model.dtos.AddUserDTO;
import de.vinado.wicket.participate.model.dtos.PersonDTO;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.wicket.request.Url;
import org.apache.wicket.util.string.Strings;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Provides interaction with the database. This service takes care of user and user related objects.
 *
 * @author Vincent Nadoll
 */
@Slf4j
@Service
@Setter(value = AccessLevel.PROTECTED, onMethod = @__(@Autowired))
public class UserService extends DataService {

    private PersonService personService;
    private EmailService emailService;

    @Override
    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Creates a new user.
     *
     * @param dto the DTO from which the user is created
     * @return the created user
     */
    @Transactional
    public User createUser(AddUserDTO dto) {
        return save(new User(dto.getUsername(), dto.getPassword(), false, true));
    }

    /**
     * Saves an existing user.
     *
     * @param dto the DTO of the user to be updated
     * @return the saved user
     */
    @Transactional
    public User saveUser(AddUserDTO dto) {
        User loadedUser = load(User.class, dto.getUser().getId());
        loadedUser.setUsername(dto.getUsername());
        if (!Strings.isEmpty(dto.getPassword())) {
            loadedUser.setPasswordSha256(null != dto.getPassword() ? DigestUtils.sha256Hex(dto.getPassword()) : null);
        }
        loadedUser.setAdmin(dto.getUser().isAdmin());
        loadedUser.setEnabled(dto.getUser().isEnabled());
        return save(loadedUser);
    }

    /**
     * Removes the user.
     *
     * @param user the user to be removed
     */
    @Transactional
    public void removeUser(User user) {
        User loadedUser = load(User.class, user.getId());
        loadedUser.setActive(false);
        save(loadedUser);
    }

    /**
     * Assigns a person to an existing user. If the person doesn't exist a new one will be created.
     *
     * @param dto the DTO of the user to be assigned; {@code user} must not be null
     * @return the saved user
     */
    @Transactional
    public User assignPerson(AddUserDTO dto) {
        User user = dto.getUser();
        if (null == user) {
            log.error("User must not be null");
            return null;
        }

        user = load(User.class, user.getId());
        Person person = dto.getPerson();

        if (null == person) {
            if (personService.hasPerson(dto.getEmail())) {
                person = personService.getPerson(dto.getEmail());
            } else {
                PersonDTO personDTO = new PersonDTO();
                personDTO.setFirstName(dto.getFirstName());
                personDTO.setLastName(dto.getLastName());
                personDTO.setEmail(dto.getEmail());
                person = personService.createPerson(personDTO);
            }
        }

        user.setPerson(person);
        return save(user);
    }

    /**
     * Creates a new user recovery token.
     *
     * @param user          the user for which the token is created
     * @param validDuration the token validity duration
     * @return the created recovery token
     */
    @Transactional
    protected UserRecoveryToken createUserRecoveryToken(User user, int validDuration) {
        return save(new UserRecoveryToken(user, generateRecoveryToken(), DateTime.now().plusDays(validDuration).toDate()));
    }

    /**
     * Retrieves all users.
     *
     * @return a list of users
     */
    public List<User> getUsers() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
        Root<User> root = criteriaQuery.from(User.class);
        criteriaQuery.where(forActive(criteriaBuilder, root));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    /**
     * Fetches all users for the filter term.
     *
     * @param usernameSubstring the substring of the {@code users} to filter for
     * @return a list of filtered users
     */
    public List<User> findUsers(String usernameSubstring) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
        Root<User> root = criteriaQuery.from(User.class);
        Predicate forTerm = criteriaBuilder.like(
            criteriaBuilder.lower(root.get("username")),
            "%" + usernameSubstring.toLowerCase().trim() + "%");
        criteriaQuery.where(forTerm, forActive(criteriaBuilder, root));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    /**
     * Retrieves the user for its ID.
     *
     * @param id the ID of the users to be retrieved
     * @return the user with the given ID
     */
    public User getUser(Long id) {
        return load(User.class, id);
    }

    /**
     * Retrieves the user for its username.
     *
     * @param username the username of the user to be retrieved
     * @return the user with the given username
     */
    public User getUser(String username) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
        Root<User> root = criteriaQuery.from(User.class);
        Predicate forUsername = criteriaBuilder.equal(root.<String>get("username"), username);
        criteriaQuery.where(forUsername);
        try {
            return entityManager.createQuery(criteriaQuery).getSingleResult();
        } catch (NoResultException e) {
            log.trace("Could not find User for username={}", username);
            return null;
        }
    }

    /**
     * Retrieves the user for its assigned person.
     *
     * @param person the assigned person of the user to retrieve
     * @return the user with the assigned person
     */
    public User getUser(Person person) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
        Root<User> root = criteriaQuery.from(User.class);
        Predicate forPerson = criteriaBuilder.equal(root.get("person"), person);
        criteriaQuery.where(forPerson, forActive(criteriaBuilder, root));
        try {
            return entityManager.createQuery(criteriaQuery).getSingleResult();
        } catch (NoResultException e) {
            log.trace("Could not find User for Person /w id={}", person.getId());
            return null;
        }
    }

    /**
     * Retrieves the user for its username or email address and the plaintext password.
     *
     * @param login         either username or email address of the user to retrieve
     * @param plainPassword the user's plaintext password
     * @return the user for his given credentials
     */
    public User getUser(String login, String plainPassword) {
        String passwordSha256 = DigestUtils.sha256Hex(plainPassword);
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
        Root<User> root = criteriaQuery.from(User.class);
        Predicate forActive = forActive(criteriaBuilder, root);
        Predicate forEnabled = criteriaBuilder.equal(root.get("enabled"), true);
        Predicate forUsername = criteriaBuilder.equal(root.get("username"), login);
        Predicate forPassword = criteriaBuilder.equal(root.get("passwordSha256"), passwordSha256);
        Join<User, Person> personJoin = root.join("person", JoinType.LEFT);
        Predicate forEmail = criteriaBuilder.equal(criteriaBuilder.lower(personJoin.get("email")), login.toLowerCase());
        criteriaQuery.where(criteriaBuilder.and(forActive, forEnabled, forPassword), criteriaBuilder.or(forUsername, forEmail));
        try {
            return entityManager.createQuery(criteriaQuery).getSingleResult();
        } catch (NoResultException e) {
            log.info("Login failed for {} and ****", login);
            return null;
        }
    }

    /**
     * @param username the user's username to be checked for existence
     * @return {@code true} if a user exist for the given username; {@code false} otherwise
     */
    public boolean hasUser(String username) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<User> root = criteriaQuery.from(User.class);
        criteriaQuery.where(criteriaBuilder.equal(root.<String>get("username"), username));
        criteriaQuery.select(criteriaBuilder.count(root));
        return 0 != entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    /**
     * @param person the assigned person of the user to be checked for existence
     * @return {@code true} if a user with the assigned person exist; {@code false} otherwise
     */
    public boolean hasUser(Person person) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<User> root = criteriaQuery.from(User.class);
        criteriaQuery.select(criteriaBuilder.count(root));
        criteriaQuery.where(criteriaBuilder.equal(root.get("person"), person));
        return 0 != entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    /**
     * @param token the user's recovery token to be checked for existence
     * @return {@code true} if a user with the assigned recovery token exist; {@code false} otherwise
     */
    public boolean hasUserRecoveryToken(String token) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<UserRecoveryToken> root = criteriaQuery.from(UserRecoveryToken.class);
        criteriaQuery.select(criteriaBuilder.count(root.get("token")));
        criteriaQuery.where(criteriaBuilder.equal(root.get("token"), token));
        return 0 != entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    /**
     * Initializes a password recovery.
     *
     * @param login   either username or email address of the user to start the password recovery for
     * @param initial whether the user is asked to set a password after registration ({@code true}) or the user lost his
     *                password wants to reset the password ({@code false})
     * @return {@code true} if the initialization succeeded; {@code false} otherwise
     */
    @Transactional
    public boolean startPasswordReset(String login, boolean initial) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
        Root<User> root = criteriaQuery.from(User.class);
        Join<User, Person> personJoin = root.join("person");
        Predicate forUsername = criteriaBuilder.equal(root.get("username"), login);
        Expression<String> emailExpr = criteriaBuilder.lower(personJoin.get("email"));
        Predicate forEmail = criteriaBuilder.equal(emailExpr, login.toLowerCase());
        criteriaQuery.where(criteriaBuilder.or(forUsername, forEmail));
        try {
            User user = entityManager.createQuery(criteriaQuery).getSingleResult();

            Person person = user.getPerson();

            ApplicationProperties properties = ParticipateApplication.get().getApplicationProperties();
            String applicationName = ParticipateApplication.get().getApplicationName();
            int validDuration = initial ? 30 : 7;
            UserRecoveryToken token = createUserRecoveryToken(user, validDuration);

            Url baseUrl = ParticipateApplication.get().getRequestedUrl();
            List<String> urlSegments = new ArrayList<>();
            urlSegments.add("resetPassword");
            Url passwordRecoveryLink = new Url();
            passwordRecoveryLink.setProtocol(baseUrl.getProtocol());
            passwordRecoveryLink.setHost(baseUrl.getHost());
            if (80 != baseUrl.getPort() && 443 != baseUrl.getPort())
                passwordRecoveryLink.setPort(baseUrl.getPort());
            passwordRecoveryLink.concatSegments(urlSegments);
            passwordRecoveryLink.setQueryParameter("token", token.getToken());

            Email email = new Email() {
                @Override
                public Map<String, Object> getData(ApplicationProperties properties) {
                    Map<String, Object> data = super.getData(properties);
                    data.put("firstName", person.getFirstName());
                    data.put("passwordRecoveryLink", passwordRecoveryLink.toString(Url.StringMode.FULL, StandardCharsets.UTF_8));
                    data.put("validDuration", validDuration);
                    return data;
                }
            };
            email.setFrom(properties.getMail().getSender(), applicationName);
            email.addTo(person.getEmail(), person.getDisplayName());
            email.setSubject(initial ? "Konto aktivieren" : "Passwort zurücksetzen");

            if (initial) {
                emailService.send(email, "newUser-txt.ftl", "newUser-html.ftl");
            } else {
                emailService.send(email, "passwordReset-txt.ftl", "passwordReset-html.ftl");
            }

            return true;
        } catch (NoResultException e) {
            log.warn("Password recovery for unknown login: {}.", login);
        } catch (UnsupportedEncodingException e) {
            log.error("Encoding is not supported", e);
        }

        return false;
    }

    /**
     * Finishes the password recovery process. A success email will be sent to the user afterwards.
     *
     * @param recoveryToken    the user's recovery token
     * @param newPlainPassword the new password to be saved
     * @return {@code true} if the finish process succeeded; {@code false} otherwise
     */
    @Transactional
    public boolean finishPasswordReset(String recoveryToken, String newPlainPassword) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<UserRecoveryToken> criteriaQuery = criteriaBuilder.createQuery(UserRecoveryToken.class);
        Root<UserRecoveryToken> root = criteriaQuery.from(UserRecoveryToken.class);
        Predicate forRecoveryToken = criteriaBuilder.equal(root.get("token"), recoveryToken);
        criteriaQuery.where(criteriaBuilder.and(forRecoveryToken));
        try {
            UserRecoveryToken token = entityManager.createQuery(criteriaQuery).getSingleResult();

            User user = token.getUser();
            user.setPasswordSha256(DigestUtils.sha256Hex(newPlainPassword));
            save(user);

            remove(token);

            ApplicationProperties properties = ParticipateApplication.get().getApplicationProperties();
            String applicationName = ParticipateApplication.get().getApplicationName();

            Person person = user.getPerson();

            Email email = new Email() {
                @Override
                public Map<String, Object> getData(ApplicationProperties applicationProperties) {
                    Map<String, Object> data = super.getData(applicationProperties);
                    data.put("firstName", person.getFirstName());
                    return data;
                }
            };
            email.setFrom(properties.getMail().getSender(), applicationName);
            email.addTo(person.getEmail(), person.getDisplayName());
            email.setSubject("Dein Passwort wurde aktualisiert");

            emailService.send(email, "passwordResetSuccess-txt.ftl", "passwordResetSuccess-html.ftl");

            return true;
        } catch (NoResultException e) {
            log.error("Invalid password recovery token for: {}.", recoveryToken);
        } catch (UnsupportedEncodingException e) {
            log.error("Encoding is not supported", e);
        }

        return false;
    }

    /**
     * @return list of all users
     */
    public List<User> getAll() {
        return getAll(User.class);
    }

    /**
     * Recursively generates an alphanumeric user recovery token.
     *
     * @return a new password recovery token
     *
     * @see #hasUserRecoveryToken(String)
     */
    private String generateRecoveryToken() {
        String recoveryToken = RandomStringUtils.randomAlphanumeric(20);

        if (hasUserRecoveryToken(recoveryToken)) {
            return generateRecoveryToken();
        } else {
            return recoveryToken;
        }
    }
}
