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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.wicket.request.Url;
import org.apache.wicket.util.string.Strings;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
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
@Primary
@Service
@Transactional
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserServiceImpl extends DataService implements UserService {

    private final PersonService personService;
    private final EmailService emailService;

    @Override
    @PersistenceContext
    public void setEntityManager(final EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User createUser(final AddUserDTO dto) {
        return save(new User(dto.getUsername(), dto.getPassword(), false, true));
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
     * {@inheritDoc}
     */
    @Override
    public void removeUser(final User user) {
        final User loadedUser = load(User.class, user.getId());
        loadedUser.setActive(false);
        save(loadedUser);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User assignPerson(final AddUserDTO dto) {
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
    protected UserRecoveryToken createUserRecoveryToken(final User user, final int validDuration) {
        return save(new UserRecoveryToken(user, generateRecoveryToken(), DateTime.now().plusDays(validDuration).toDate()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<User> getUsers() {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
        final Root<User> root = criteriaQuery.from(User.class);
        criteriaQuery.where(forActive(criteriaBuilder, root));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
     * {@inheritDoc}
     */
    @Override
    public User getUser(final Long id) {
        return load(User.class, id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User getUser(final String username) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
        final Root<User> root = criteriaQuery.from(User.class);
        final Predicate forUsername = criteriaBuilder.equal(root.<String>get("username"), username);
        criteriaQuery.where(forUsername);
        try {
            return entityManager.createQuery(criteriaQuery).getSingleResult();
        } catch (final NoResultException e) {
            log.trace("Could not find User for username={}", username);
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public User getUser(final Person person) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
        final Root<User> root = criteriaQuery.from(User.class);
        final Predicate forPerson = criteriaBuilder.equal(root.get("person"), person);
        criteriaQuery.where(forPerson, forActive(criteriaBuilder, root));
        try {
            return entityManager.createQuery(criteriaQuery).getSingleResult();
        } catch (final NoResultException e) {
            log.trace("Could not find User for Person /w id={}", person.getId());
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
            log.info("Login failed for {} and ****", usernameOrEmail);
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasUser(final String username) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        final Root<User> root = criteriaQuery.from(User.class);
        criteriaQuery.where(criteriaBuilder.equal(root.<String>get("username"), username));
        criteriaQuery.select(criteriaBuilder.count(root));
        return 0 != entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasUser(final Person person) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        final Root<User> root = criteriaQuery.from(User.class);
        criteriaQuery.select(criteriaBuilder.count(root));
        criteriaQuery.where(criteriaBuilder.equal(root.get("person"), person));
        return 0 != entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasUserRecoveryToken(final String token) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        final Root<UserRecoveryToken> root = criteriaQuery.from(UserRecoveryToken.class);
        criteriaQuery.select(criteriaBuilder.count(root.get("token")));
        criteriaQuery.where(criteriaBuilder.equal(root.get("token"), token));
        return 0 != entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
            log.warn("Password recovery for unknown login: {}.", usernameOrEmail);
        } catch (TemplateException | IOException e) {
            log.error("Unable to parse Freemarker template", e);
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
            log.error("Invalid password recovery token for: {}.", recoveryToken);
        } catch (TemplateException | IOException e) {
            log.error("Unable to parse Freemarker template", e);
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<User> getAll() {
        return getAll(User.class);
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
