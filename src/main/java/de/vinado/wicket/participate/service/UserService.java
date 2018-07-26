package de.vinado.wicket.participate.service;

import de.vinado.wicket.participate.ParticipateApplication;
import de.vinado.wicket.participate.configuration.ApplicationProperties;
import de.vinado.wicket.participate.data.Person;
import de.vinado.wicket.participate.data.User;
import de.vinado.wicket.participate.data.UserRecoveryToken;
import de.vinado.wicket.participate.data.dto.AddUserDTO;
import de.vinado.wicket.participate.data.email.MailData;
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
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
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
     * Creates a new {@link de.vinado.wicket.participate.data.User}
     *
     * @param dto {@link de.vinado.wicket.participate.data.dto.AddUserDTO}
     * @return Created user
     */
    @Transactional
    public User createUserWithPassword(final AddUserDTO dto) {
        User user = new User(dto.getUsername(), DigestUtils.sha256Hex(dto.getPassword()), false, true);
        user = save(user);
        return user;
    }

    @Transactional
    public User createUser(final AddUserDTO dto) {
        return save(new User(dto.getUsername(), null, false, true));
    }

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

    @Transactional
    public Person assignOrCreatePersonForUser(final AddUserDTO dto) {
        final User loadedUser = load(User.class, dto.getUser().getId());
        final Person person;

        if (null != dto.getPerson()) {
            person = dto.getPerson();
        } else {
            person = personService.getOrCreatePerson(new Person(dto.getFirstName(), dto.getLastName(), dto.getEmail()));
        }

        if (null != person && null != loadedUser) {
            loadedUser.setPerson(person);
            save(loadedUser);
            return person;
        }
        return null;
    }

    @Transactional
    public User saveUserAndPerson(final AddUserDTO dto) {
        final User user = new User(dto.getUsername(), null, false, true);

        if (null != dto.getEmail() && null != dto.getFirstName() && null != dto.getLastName()) {
            Person person = new Person(dto.getFirstName(), dto.getLastName(), dto.getEmail());
            person = save(person);
            user.setPerson(person);
        }
        return save(user);
    }

    @Transactional
    public User getOrCreateUser(final String username) {
        final User user = getUser4Username(username);
        if (null != user) {
            return user;
        } else {
            return save(new User(username, null, false, true));
        }
    }

    @Transactional
    public void removeUser(final User user) {
        //remove(load(User.class, user.getId()));

        final User loadedUser = load(User.class, user.getId());
        loadedUser.setAdmin(false);
        save(loadedUser);
    }

    @Transactional
    public void removePersonFromUser(final User user) {
        final User loadedUser = load(User.class, user.getId());
        loadedUser.setPerson(null);
        save(loadedUser);
    }

    @Transactional
    public boolean startPasswordReset(final String usernameOrEmail, boolean initail) {
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
            final int validDuration = initail ? 30 : 7;
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
            mailData.setSubject(initail ? "Konto aktivieren" : "Passwort zurücksetzen");

            emailService.send(mailData, initail ? "fm-userInvite.ftl" : "fm-passwordReset.ftl", true);
            return true;
        } catch (final NoResultException e) {
            LOGGER.warn("Password recovery for unknown login: {}.", usernameOrEmail);
        }
        return false;
    }

    @Transactional
    protected UserRecoveryToken createUserRecoveryToken(final User user, final int validDuration) {
        return save(new UserRecoveryToken(user, RandomStringUtils.randomAlphanumeric(20), DateTime.now().plusDays(validDuration).toDate()));
    }

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

            emailService.send(mailData, "fm-passwordResetS.ftl", true);

            return true;
        } catch (final NoResultException e) {
            LOGGER.error("Invalid password recovery token for: {}.", recoveryToken);
            return false;
        }
    }

    /**
     * @param username Username
     * @return User
     */
    public User getUser4Username(final String username) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
        final Root<User> root = criteriaQuery.from(User.class);
        final Predicate forUsername = criteriaBuilder.equal(root.<String>get("username"), username);
        final Predicate forActive = criteriaBuilder.equal(root.<Boolean>get("enabled"), true);
        criteriaQuery.where(forActive, forUsername);
        try {
            return entityManager.createQuery(criteriaQuery).getSingleResult();
        } catch (final NoResultException e) {
            LOGGER.info("{} could not be found.", username);
            return null;
        }
    }

    public User getUser4PersonId(final Long personId) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
        final Root<User> root = criteriaQuery.from(User.class);
        final Join<User, Person> personJoin = root.join("person");
        final Predicate forActive = criteriaBuilder.equal(root.get("enabled"), true);
        final Predicate forPersonId = criteriaBuilder.equal(personJoin.get("id"), personId);
        criteriaQuery.where(forActive, forPersonId);
        try {
            return entityManager.createQuery(criteriaQuery).getSingleResult();
        } catch (final NoResultException e) {
            LOGGER.info("Person with id={}, is not an user.", personId);
            return null;
        }
    }

    public boolean hasUser(final String username) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        final Root<User> root = criteriaQuery.from(User.class);
        criteriaQuery.where(criteriaBuilder.equal(root.<String>get("username"), username));
        criteriaQuery.select(criteriaBuilder.count(root));
        return 0 != entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    /**
     * @param usernameOrEmail Username or person email address
     * @param plainPassword   Password in plain text
     * @return User, if the credential are correct
     */
    public User getAuthenticatedUser(final String usernameOrEmail, final String plainPassword) {
        final String passwordSha256 = DigestUtils.sha256Hex(plainPassword);
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
        final Root<User> root = criteriaQuery.from(User.class);
        final Predicate forEnabled = criteriaBuilder.equal(root.get("enabled"), true);
        final Predicate forUsername = criteriaBuilder.equal(root.get("username"), usernameOrEmail);
        final Predicate forPassword = criteriaBuilder.equal(root.get("passwordSha256"), passwordSha256);
        final Join<User, Person> personJoin = root.join("person", JoinType.LEFT);
        final Predicate forEmail = criteriaBuilder.equal(criteriaBuilder.lower(personJoin.get("email")), usernameOrEmail.toLowerCase());
        criteriaQuery.where(criteriaBuilder.and(forEnabled, forPassword), criteriaBuilder.or(forUsername, forEmail));
        try {
            return entityManager.createQuery(criteriaQuery).getSingleResult();
        } catch (NoResultException e) {
            LOGGER.info("Login failed for {} and ****", usernameOrEmail);
            return null;
        }
    }

    public List<User> findUsers(final String term) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
        final Root<User> root = criteriaQuery.from(User.class);
        final Predicate forTerm = criteriaBuilder.like(
                criteriaBuilder.lower(root.get("searchName")),
                "%" + term.toLowerCase().trim() + "%");
        criteriaQuery.where(forTerm);
        return entityManager.createQuery(criteriaQuery).setMaxResults(5).getResultList();
    }

    public boolean isPersonAssigned(final Person person) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        final Root<User> root = criteriaQuery.from(User.class);
        criteriaQuery.select(criteriaBuilder.count(root));
        criteriaQuery.where(criteriaBuilder.equal(root.get("person"), person));
        return 0 != entityManager.createQuery(criteriaQuery).getSingleResult();
    }
}
