package de.vinado.wicket.participate.services;

import de.vinado.wicket.participate.ParticipateApplication;
import de.vinado.wicket.participate.configuration.ApplicationProperties;
import de.vinado.wicket.participate.model.Person;
import de.vinado.wicket.participate.model.User;
import de.vinado.wicket.participate.model.UserRecoveryToken;
import de.vinado.wicket.participate.model.dtos.AddUserDTO;
import de.vinado.wicket.participate.model.dtos.PersonDTO;
import de.vinado.wicket.participate.model.email.MailData;
import freemarker.template.TemplateException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.wicket.request.Url;
import org.apache.wicket.util.string.Strings;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Provides interaction with the database. This service takes care of {@link User} and user related objects.
 *
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
@Service
public class UserService extends DataService {

    private final static Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private PersonService personService;

    @Autowired
    private EmailService emailService;

    @PersistenceContext
    public void setEntityManager(final EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Creates a new {@link User}.
     *
     * @param dto {@link AddUserDTO}
     * @return Saved {@link User}
     */
    @Transactional
    public User createUser(final AddUserDTO dto) {
        return save(new User(dto.getUsername(), dto.getPassword(), false, true));
    }

    /**
     * Saves an existing {@link User}.
     *
     * @param dto {@link AddUserDTO}
     * @return Saved {@link User}
     */
    @Transactional
    public User saveUser(final AddUserDTO dto) {
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
     * Sets an {@link User} to inactive.
     *
     * @param user {@link User}
     */
    @Transactional
    public void removeUser(final User user) {
        final User loadedUser = load(User.class, user.getId());
        loadedUser.setActive(false);
        save(loadedUser);
    }

    /**
     * Assigns a {@link Person} to an {@link User}. If {@link AddUserDTO#person} is null a new {@link Person} will be
     * created. If {@link Person#email} is already in user the {@link Person} will be used instead of creating a new
     * one. User must not be null.
     *
     * @param dto {@link AddUserDTO}
     * @return Saved {@link User}
     */
    @Transactional
    public User assignPerson(final AddUserDTO dto) {
        User user = dto.getUser();
        if (null == user) {
            LOGGER.error("User must not be null");
            return null;
        }

        user = load(User.class, user.getId());
        Person person = dto.getPerson();

        if (null == person) {
            if (personService.hasPerson(dto.getEmail())) {
                person = personService.getPerson(dto.getEmail());
            } else {
                final PersonDTO personDTO = new PersonDTO();
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
     * Creates a new {@link UserRecoveryToken}.
     *
     * @param user          {@link User}
     * @param validDuration Duration of validity in days.
     * @return Saved {@link UserRecoveryToken}
     */
    @Transactional
    protected UserRecoveryToken createUserRecoveryToken(final User user, final int validDuration) {
        return save(new UserRecoveryToken(user, generateRecoveryToken(), DateTime.now().plusDays(validDuration).toDate()));
    }

    /**
     * Fetches all {@link User}s.
     *
     * @return List of {@link User}s
     */
    public List<User> getUsers() {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
        final Root<User> root = criteriaQuery.from(User.class);
        criteriaQuery.where(forActive(criteriaBuilder, root));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    /**
     * Fetches all {@link User}s for the filter term.
     *
     * @param term Filter term
     * @return List of filtered {@link User}s
     */
    public List<User> findUsers(final String term) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
        final Root<User> root = criteriaQuery.from(User.class);
        final Predicate forTerm = criteriaBuilder.like(
            criteriaBuilder.lower(root.get("username")),
            "%" + term.toLowerCase().trim() + "%");
        criteriaQuery.where(forTerm, forActive(criteriaBuilder, root));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    /**
     * Fetches an {@link User} for {@link User#username}.
     *
     * @param username {@link User#username}
     * @return {@link User} for {@link User#username}
     */
    public User getUser(final String username) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
        final Root<User> root = criteriaQuery.from(User.class);
        final Predicate forUsername = criteriaBuilder.equal(root.<String>get("username"), username);
        criteriaQuery.where(forUsername);
        try {
            return entityManager.createQuery(criteriaQuery).getSingleResult();
        } catch (final NoResultException e) {
            LOGGER.trace("Could not find User for username={}", username);
            return null;
        }
    }

    /**
     * Fetches an {@link User} for {@link Person}.
     *
     * @param person {@link Person}
     * @return {@link User} for {@link Person}
     */
    public User getUser(final Person person) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
        final Root<User> root = criteriaQuery.from(User.class);
        final Predicate forPerson = criteriaBuilder.equal(root.get("person"), person);
        criteriaQuery.where(forPerson, forActive(criteriaBuilder, root));
        try {
            return entityManager.createQuery(criteriaQuery).getSingleResult();
        } catch (final NoResultException e) {
            LOGGER.trace("Could not find User for Person /w id={}", person.getId());
            return null;
        }
    }

    /**
     * Fetches an {@link User} for {@link User#username} or {@link Person#email} and the plaintext password from the
     * login form. This method is used to authenticate an User.
     *
     * @param usernameOrEmail {@link User#username} or {@link Person#email}
     * @param plainPassword   {@link User#passwordSha256} in plain text
     * @return {@link User}, if the credential are correct
     */
    public User getUser(final String usernameOrEmail, final String plainPassword) {
        final String passwordSha256 = DigestUtils.sha256Hex(plainPassword);
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
        final Root<User> root = criteriaQuery.from(User.class);
        final Predicate forActive = forActive(criteriaBuilder, root);
        final Predicate forEnabled = criteriaBuilder.equal(root.get("enabled"), true);
        final Predicate forUsername = criteriaBuilder.equal(root.get("username"), usernameOrEmail);
        final Predicate forPassword = criteriaBuilder.equal(root.get("passwordSha256"), passwordSha256);
        final Join<User, Person> personJoin = root.join("person", JoinType.LEFT);
        final Predicate forEmail = criteriaBuilder.equal(criteriaBuilder.lower(personJoin.get("email")), usernameOrEmail.toLowerCase());
        criteriaQuery.where(criteriaBuilder.and(forActive, forEnabled, forPassword), criteriaBuilder.or(forUsername, forEmail));
        try {
            return entityManager.createQuery(criteriaQuery).getSingleResult();
        } catch (NoResultException e) {
            LOGGER.info("Login failed for {} and ****", usernameOrEmail);
            return null;
        }
    }

    /**
     * Return whether the {@link User} exists for {@link User#username}.
     *
     * @param username {@link User#username}
     * @return Whether the {@link User} exists for {@link User#username}
     */
    public boolean hasUser(final String username) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        final Root<User> root = criteriaQuery.from(User.class);
        criteriaQuery.where(criteriaBuilder.equal(root.<String>get("username"), username));
        criteriaQuery.select(criteriaBuilder.count(root));
        return 0 != entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    /**
     * Return whether the {@link User} exists for {@link User#person}.
     *
     * @param person {@link Person}
     * @return Whether the {@link User} exists for {@link User#person}
     */
    public boolean hasUser(final Person person) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        final Root<User> root = criteriaQuery.from(User.class);
        criteriaQuery.select(criteriaBuilder.count(root));
        criteriaQuery.where(criteriaBuilder.equal(root.get("person"), person));
        return 0 != entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    /**
     * Return whether the {@link User} exists for {@link UserRecoveryToken#token}.
     *
     * @param token {@link UserRecoveryToken#token}
     * @return Whether the {@link User} exists for {@link UserRecoveryToken#token}
     */
    public boolean hasUserRecoveryToken(final String token) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        final Root<UserRecoveryToken> root = criteriaQuery.from(UserRecoveryToken.class);
        criteriaQuery.select(criteriaBuilder.count(root.get("token")));
        criteriaQuery.where(criteriaBuilder.equal(root.get("token"), token));
        return 0 != entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    /**
     * Sends an email to the {@link User} of {@link User#username} or {@link Person#email} with an password reset link.
     *
     * @param usernameOrEmail {@link User#username} or {@link Person#email}
     * @param initial         Whether the invitation is a common password reset or an initial one.
     * @return Whether the email has been sent.
     */
    @Transactional
    public boolean startPasswordReset(final String usernameOrEmail, boolean initial) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
        final Root<User> root = criteriaQuery.from(User.class);
        final Join<User, Person> personJoin = root.join("person");
        final Predicate forUsername = criteriaBuilder.equal(root.get("username"), usernameOrEmail);
        final Expression<String> emailExpr = criteriaBuilder.lower(personJoin.get("email"));
        final Predicate forEmail = criteriaBuilder.equal(emailExpr, usernameOrEmail.toLowerCase());
        criteriaQuery.where(criteriaBuilder.or(forUsername, forEmail));
        try {
            final User user = entityManager.createQuery(criteriaQuery).getSingleResult();

            final Person person = user.getPerson();

            final ApplicationProperties properties = ParticipateApplication.get().getApplicationProperties();
            final String applicationName = ParticipateApplication.get().getApplicationName();
            final int validDuration = initial ? 30 : 7;
            final UserRecoveryToken token = createUserRecoveryToken(user, validDuration);

            final Url baseUrl = ParticipateApplication.get().getRequestedUrl();
            final List<String> urlSegments = new ArrayList<>();
            urlSegments.add("resetPassword");
            final Url passwordRecoveryLink = new Url();
            passwordRecoveryLink.setProtocol(baseUrl.getProtocol());
            passwordRecoveryLink.setHost(baseUrl.getHost());
            if (80 != baseUrl.getPort() && 443 != baseUrl.getPort())
                passwordRecoveryLink.setPort(baseUrl.getPort());
            passwordRecoveryLink.concatSegments(urlSegments);
            passwordRecoveryLink.setQueryParameter("token", token.getToken());

            final MailData mailData = new MailData() {
                @Override
                public Map<String, Object> getData() {
                    final Map<String, Object> data = super.getData();
                    data.put("firstName", person.getFirstName());
                    data.put("passwordRecoveryLink", passwordRecoveryLink.toString(Url.StringMode.FULL, Charset.defaultCharset()));
                    data.put("validDuration", validDuration);
                    return data;
                }
            };
            mailData.setFrom(properties.getMail().getSender(), applicationName);
            mailData.addTo(person.getEmail(), person.getDisplayName());
            mailData.setSubject(initial ? "Konto aktivieren" : "Passwort zurücksetzen");

            if (initial) {
                emailService.send(mailData, "newUser-txt.ftl", "newUser-html.ftl");
            } else {
                emailService.send(mailData, "passwordReset-txt.ftl", "passwordReset-html.ftl");
            }

            return true;
        } catch (NoResultException e) {
            LOGGER.warn("Password recovery for unknown login: {}.", usernameOrEmail);
        } catch (TemplateException | IOException e) {
            LOGGER.error("Unable to parse Freemarker template", e);
        }

        return false;
    }

    /**
     * Saves the {@link User} with a new {@link User#passwordSha256} and sends an email when the password reset is
     * finished. The {@link UserRecoveryToken} will be removed from the database.
     *
     * @param recoveryToken    {@link UserRecoveryToken#token} from the email link
     * @param newPlainPassword {@link User#passwordSha256} in plaintext
     * @return Whether the email has benn sent successfully
     */
    @Transactional
    public boolean finishPasswordReset(final String recoveryToken, final String newPlainPassword) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<UserRecoveryToken> criteriaQuery = criteriaBuilder.createQuery(UserRecoveryToken.class);
        final Root<UserRecoveryToken> root = criteriaQuery.from(UserRecoveryToken.class);
        final Predicate forRecoveryToken = criteriaBuilder.equal(root.get("token"), recoveryToken);
        criteriaQuery.where(criteriaBuilder.and(forRecoveryToken));
        try {
            final UserRecoveryToken token = entityManager.createQuery(criteriaQuery).getSingleResult();

            final User user = token.getUser();
            user.setPasswordSha256(DigestUtils.sha256Hex(newPlainPassword));
            save(user);

            remove(token);

            final ApplicationProperties properties = ParticipateApplication.get().getApplicationProperties();
            final String applicationName = ParticipateApplication.get().getApplicationName();

            final Person person = user.getPerson();

            final MailData mailData = new MailData() {
                @Override
                public Map<String, Object> getData() {
                    final Map<String, Object> data = super.getData();
                    data.put("firstName", person.getFirstName());
                    return data;
                }
            };
            mailData.setFrom(properties.getMail().getSender(), applicationName);
            mailData.addTo(person.getEmail(), person.getDisplayName());
            mailData.setSubject("Dein Passwort wurde aktualisiert");

            emailService.send(mailData, "passwordResetSuccess-txt.ftl", "passwordResetSuccess-html.ftl");

            return true;
        } catch (NoResultException e) {
            LOGGER.error("Invalid password recovery token for: {}.", recoveryToken);
        } catch (TemplateException | IOException e) {
            LOGGER.error("Unable to parse Freemarker template", e);
        }

        return false;
    }

    /**
     * Generates a new alphanumeric {@link UserRecoveryToken#token}
     * with a length of 20 chars. If the token already exists the function calls itself .
     *
     * @return Password reset token
     * @see #hasUserRecoveryToken(String)
     */
    private String generateRecoveryToken() {
        final String recoveryToken = RandomStringUtils.randomAlphanumeric(20);

        if (hasUserRecoveryToken(recoveryToken)) {
            return generateRecoveryToken();
        } else {
            return recoveryToken;
        }
    }
}
