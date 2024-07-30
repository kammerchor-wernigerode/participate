package de.vinado.wicket.participate.services;

import de.vinado.wicket.participate.email.Email;
import de.vinado.wicket.participate.email.EmailBuilderFactory;
import de.vinado.wicket.participate.email.service.EmailService;
import de.vinado.wicket.participate.model.Person;
import de.vinado.wicket.participate.model.User;
import de.vinado.wicket.participate.model.UserRecoveryToken;
import de.vinado.wicket.participate.model.dtos.AddUserDTO;
import de.vinado.wicket.participate.model.dtos.PersonDTO;
import de.vinado.wicket.participate.wicket.inject.RequestUrl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.wicket.request.Url;
import org.apache.wicket.util.string.Strings;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Primary
@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl extends DataService implements UserService {

    private final PersonService personService;
    private final EmailService emailService;
    private final EmailBuilderFactory emailBuilderFactory;
    private final RequestUrl requestUrl;

    @Override
    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public User createUser(AddUserDTO dto) {
        return save(new User(dto.getUsername(), dto.getPassword(), false, true));
    }

    @Override
    public User saveUser(AddUserDTO dto) {
        User loadedUser = load(User.class, dto.getUser().getId());
        loadedUser.setUsername(dto.getUsername());
        if (!Strings.isEmpty(dto.getPassword())) {
            loadedUser.setPasswordSha256(null != dto.getPassword() ? DigestUtils.sha256Hex(dto.getPassword()) : null);
        }
        loadedUser.setPerson(dto.getPerson());
        loadedUser.setAdmin(dto.getUser().isAdmin());
        loadedUser.setEnabled(dto.getUser().isEnabled());
        return save(loadedUser);
    }

    @Override
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

    protected UserRecoveryToken createUserRecoveryToken(User user, int validDuration) {
        LocalDateTime validUntil = LocalDateTime.now().plusDays(validDuration);
        Instant instant = validUntil.atZone(ZoneId.systemDefault()).toInstant();
        return save(new UserRecoveryToken(user, generateRecoveryToken(), Date.from(instant)));
    }

    @Override
    public List<User> getUsers() {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
        Root<User> root = criteriaQuery.from(User.class);
        criteriaQuery.where(forActive(criteriaBuilder, root));
        return entityManager.createQuery(criteriaQuery).getResultList();
    }

    @Override
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

    @Override
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

    @Override
    public boolean hasUser(String username) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<User> root = criteriaQuery.from(User.class);
        criteriaQuery.where(criteriaBuilder.equal(root.<String>get("username"), username));
        criteriaQuery.select(criteriaBuilder.count(root));
        return 0 != entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    @Override
    public boolean hasUser(Person person) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<User> root = criteriaQuery.from(User.class);
        criteriaQuery.select(criteriaBuilder.count(root));
        criteriaQuery.where(criteriaBuilder.equal(root.get("person"), person));
        return 0 != entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    @Override
    public boolean hasUserRecoveryToken(String token) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<UserRecoveryToken> root = criteriaQuery.from(UserRecoveryToken.class);
        criteriaQuery.select(criteriaBuilder.count(root.get("token")));
        criteriaQuery.where(criteriaBuilder.equal(root.get("token"), token));
        return 0 != entityManager.createQuery(criteriaQuery).getSingleResult();
    }

    @Override
    public boolean startPasswordReset(String usernameOrEmail, boolean initial) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
        Root<User> root = criteriaQuery.from(User.class);
        Join<User, Person> personJoin = root.join("person");
        Predicate forUsername = criteriaBuilder.equal(root.get("username"), usernameOrEmail);
        Expression<String> emailExpr = criteriaBuilder.lower(personJoin.get("email"));
        Predicate forEmail = criteriaBuilder.equal(emailExpr, usernameOrEmail.toLowerCase());
        criteriaQuery.where(criteriaBuilder.or(forUsername, forEmail));
        try {
            User user = entityManager.createQuery(criteriaQuery).getSingleResult();

            Person person = user.getPerson();

            int validDuration = initial ? 30 : 7;
            UserRecoveryToken token = createUserRecoveryToken(user, validDuration);

            URL baseUrl = requestUrl.get();
            List<String> urlSegments = new ArrayList<>();
            urlSegments.add("resetPassword");
            Url passwordRecoveryLink = new Url();
            passwordRecoveryLink.setProtocol(baseUrl.getProtocol());
            passwordRecoveryLink.setHost(baseUrl.getHost());
            if (80 != baseUrl.getPort() && 443 != baseUrl.getPort())
                passwordRecoveryLink.setPort(baseUrl.getPort());
            passwordRecoveryLink.concatSegments(urlSegments);
            passwordRecoveryLink.setQueryParameter("token", token.getToken());

            Email email = emailBuilderFactory.create()
                .to(person)
                .data("firstName", person.getFirstName())
                .data("passwordRecoveryLink", passwordRecoveryLink.toString(Url.StringMode.FULL, StandardCharsets.UTF_8))
                .data("validDuration", validDuration)
                .subject(initial ? "Konto aktivieren" : "Passwort zurücksetzen")
                .build();
            if (initial) {
                emailService.send(email, "newUser-txt.ftl", "newUser-html.ftl");
            } else {
                emailService.send(email, "passwordReset-txt.ftl", "passwordReset-html.ftl");
            }

            return true;
        } catch (NoResultException e) {
            log.warn("Password recovery for unknown login: {}.", usernameOrEmail);
        }

        return false;
    }

    @Override
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

            Person person = user.getPerson();

            Email email = emailBuilderFactory.create()
                .to(person)
                .subject("Dein Passwort wurde aktualisiert")
                .data("firstName", person.getFirstName())
                .build();

            emailService.send(email, "passwordResetSuccess-txt.ftl", "passwordResetSuccess-html.ftl");

            return true;
        } catch (NoResultException e) {
            log.error("Invalid password recovery token for: {}.", recoveryToken);
        }

        return false;
    }

    @Override
    public List<User> getAll() {
        return getAll(User.class);
    }

    private String generateRecoveryToken() {
        String recoveryToken = RandomStringUtils.randomAlphanumeric(20);

        if (hasUserRecoveryToken(recoveryToken)) {
            return generateRecoveryToken();
        } else {
            return recoveryToken;
        }
    }
}
