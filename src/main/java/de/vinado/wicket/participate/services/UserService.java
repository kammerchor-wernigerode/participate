package de.vinado.wicket.participate.services;

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
import org.apache.wicket.util.string.Strings;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.persistence.NoResultException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
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
public class UserService {

    private UserRepository userRepository;
    private UserRecoveryTokenRepository userRecoveryTokenRepository;

    private PersonService personService;
    private EmailService emailService;
    private ApplicationProperties applicationProperties;

    @Setter(AccessLevel.NONE)
    @Value("${spring.application.name:KCH Participate}")
    private String applicationName;

    /**
     * Creates a new user.
     *
     * @param dto the DTO from which the user is created
     * @return the created user
     */
    @Transactional
    public User create(AddUserDTO dto) {
        return userRepository.save(new User(dto.getUsername(), dto.getPassword(), false, true));
    }

    /**
     * Saves an existing user.
     *
     * @param dto the DTO of the user to be updated
     * @return the saved user
     *
     * @throws NoResultException in case the user could not be found
     */
    @Transactional
    public User save(AddUserDTO dto) throws NoResultException {
        User loadedUser = userRepository.findById(dto.getUser().getId()).orElseThrow(NoResultException::new);
        loadedUser.setUsername(dto.getUsername());
        if (!Strings.isEmpty(dto.getPassword())) {
            loadedUser.setPasswordSha256(null != dto.getPassword() ? DigestUtils.sha256Hex(dto.getPassword()) : null);
        }
        loadedUser.setAdmin(dto.getUser().isAdmin());
        loadedUser.setEnabled(dto.getUser().isEnabled());
        return userRepository.save(loadedUser);
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
        Assert.notNull(user, "The given dto.user must not be null!");

        user = userRepository.findById(user.getId()).orElseThrow(NoResultException::new);
        Person person = dto.getPerson();

        if (null == person) {
            if (personService.personExist(dto.getEmail())) {
                person = personService.retrievePerson(dto.getEmail());
            } else {
                PersonDTO personDTO = new PersonDTO();
                personDTO.setFirstName(dto.getFirstName());
                personDTO.setLastName(dto.getLastName());
                personDTO.setEmail(dto.getEmail());
                person = personService.create(personDTO);
            }
        }

        user.setPerson(person);
        return userRepository.save(user);
    }

    /**
     * Creates a new user recovery token.
     *
     * @param user          the user for which the token is created
     * @param validDuration the token validity duration
     * @return the created recovery token
     */
    @Transactional
    protected UserRecoveryToken create(User user, int validDuration) {
        UserRecoveryToken token = new UserRecoveryToken(user, generateRecoveryToken(),
            DateTime.now().plusDays(validDuration).toDate());
        return userRecoveryTokenRepository.save(token);
    }

    /**
     * Retrieves all users.
     *
     * @return a list of users
     */
    public List<User> list() {
        return userRepository.findAll();
    }

    /**
     * Fetches all users for the filter term.
     *
     * @param usernameSubstring the substring of the {@code users} to filter for
     * @return a list of filtered users
     */
    public List<User> findUsers(String usernameSubstring) {
        return userRepository.findAllByUsernameLikeIgnoreCase("%" + usernameSubstring + "%");
    }

    /**
     * Retrieves the user for its ID.
     *
     * @param id the ID of the users to be retrieved
     * @return the user with the given ID
     */
    public User retrieve(Long id) {
        return userRepository.findById(id).orElseThrow(NoResultException::new);
    }

    /**
     * Retrieves the user for its username.
     *
     * @param username the username of the user to be retrieved
     * @return the user with the given username
     *
     * @throws NoResultException in case the user could not be found
     */
    public User retrieve(String username) throws NoResultException {
        return userRepository.findByUsername(username).orElseThrow(NoResultException::new);
    }

    /**
     * Retrieves the user for its assigned person.
     *
     * @param person the assigned person of the user to retrieve
     * @return the user with the assigned person
     *
     * @throws NoResultException in case the user could not be found
     */
    public User retrieve(Person person) throws NoResultException {
        return userRepository.findByPerson(person).orElseThrow(NoResultException::new);
    }

    /**
     * Retrieves the user for its username or email address and the plaintext password.
     *
     * @param login         either username or email address of the user to retrieve
     * @param plainPassword the user's plaintext password
     * @return the user for his given credentials
     *
     * @throws NoResultException in case the user could not be authenticated
     */
    public User retrieve(String login, String plainPassword) throws NoResultException {
        String password = DigestUtils.sha256Hex(plainPassword);
        return userRepository.authenticate(login, password).orElseThrow(NoResultException::new);
    }

    /**
     * @param username the user's username to be checked for existence
     * @return {@code true} if a user exist for the given username; {@code false} otherwise
     */
    public boolean exist(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * @param person the assigned person of the user to be checked for existence
     * @return {@code true} if a user with the assigned person exist; {@code false} otherwise
     */
    public boolean exist(Person person) {
        return userRepository.existsByPerson(person);
    }

    /**
     * @param token the user's recovery token to be checked for existence
     * @return {@code true} if a user with the assigned recovery token exist; {@code false} otherwise
     */
    public boolean userRecoveryTokenExist(String token) {
        return userRecoveryTokenRepository.existsByToken(token);
    }

    /**
     * Initializes the user registration process. An email will be sent to the given user requesting to set his password
     * within 30 days to be able to login.
     *
     * @param user    the user to send the registration email to
     * @param subject the subject of the registration email
     */
    @Transactional
    public void initializeUserRegistration(User user, String subject) {
        final int validityDuration = 30;
        UserRecoveryToken token = create(user, validityDuration);
        Email email = preparePasswordReset(token, validityDuration, subject);
        emailService.send(email, "newUser-txt.ftl", "newUser-html.ftl");
    }

    /**
     * Initializes the user password recovery process. An email will be sent to the given email address, with a password
     * reset link on which the user can change his password.
     *
     * @param login   the users login; {@code username} or {@code person.email}
     * @param subject the subject of the recovery email
     * @throws NoResultException in case the user could not be found for the given login
     */
    @Transactional
    public void initializePasswordRecovery(String login, String subject) throws NoResultException {
        final int validityDuration = 7;
        User user = userRepository.findByLogin(login).orElseThrow(NoResultException::new);
        UserRecoveryToken token = create(user, validityDuration);
        Email message = preparePasswordReset(token, validityDuration, subject);
        emailService.send(message, "passwordReset-txt.ftl", "passwordReset-html.ftl");
    }

    /**
     * Prepares the password reset MIME message
     *
     * @param token            the password reset token
     * @param validityDuration the duration (days) in which the token is valid
     * @param subject          the message's subject
     * @return prepared password reset email
     */
    @Transactional
    protected Email preparePasswordReset(UserRecoveryToken token, int validityDuration, String subject) {
        try {
            URL baseUrl = new URL(applicationProperties.getBaseUrl());
            URI recoveryUri = new URI(baseUrl.getProtocol(), null, baseUrl.getHost(), baseUrl.getPort(),
                "/resetPassword", String.format("token=%s", token.getToken()), null);

            Person person = token.getUser().getPerson();
            Email email = new Email() {
                @Override
                public Map<String, Object> getData(ApplicationProperties properties) {
                    Map<String, Object> data = super.getData(properties);
                    data.put("firstName", person.getFirstName());
                    data.put("passwordRecoveryLink", recoveryUri.toString());
                    data.put("validDuration", validityDuration);
                    return data;
                }
            };
            email.setFrom(applicationProperties.getMail().getSender(), applicationName);
            email.addTo(person.getEmail(), person.getDisplayName());
            email.setSubject(subject);

            return email;
        } catch (UnsupportedEncodingException e) {
            log.error("Encountered unsupported character encoding", e);
        } catch (URISyntaxException | MalformedURLException e) {
            log.error("Encountered malformed URI/URL", e);
        }

        return null;
    }

    /**
     * Finishes the password recovery process. A success email will be sent to the user afterwards.
     *
     * @param recoveryToken       the user's recovery token
     * @param newPlainPassword    the new password to be hashed and saved
     * @param successEmailSubject the subject of the success email sent to the user
     * @throws NoResultException in case the token could not be removed
     */
    @Transactional
    public void finishPasswordReset(String recoveryToken, String newPlainPassword, String successEmailSubject)
        throws NoResultException {
        try {
            UserRecoveryToken token = userRecoveryTokenRepository.findByToken(recoveryToken)
                .orElseThrow(NoResultException::new);
            User user = token.getUser();
            user.setPasswordSha256(DigestUtils.sha256Hex(newPlainPassword));
            userRepository.save(user);

            userRecoveryTokenRepository.delete(token);

            Person person = user.getPerson();
            Email email = new Email() {
                @Override
                public Map<String, Object> getData(ApplicationProperties applicationProperties) {
                    Map<String, Object> data = super.getData(applicationProperties);
                    data.put("firstName", person.getFirstName());
                    return data;
                }
            };
            email.setFrom(applicationProperties.getMail().getSender(), applicationName);
            email.addTo(person.getEmail(), person.getDisplayName());
            email.setSubject(successEmailSubject);

            emailService.send(email, "passwordResetSuccess-txt.ftl", "passwordResetSuccess-html.ftl");
        } catch (UnsupportedEncodingException e) {
            log.error("Encountered unsupported character encoding", e);
        }
    }

    /**
     * Recursively generates an alphanumeric user recovery token.
     *
     * @return a new password recovery token
     *
     * @see #userRecoveryTokenExist(String)
     */
    private String generateRecoveryToken() {
        String recoveryToken = RandomStringUtils.randomAlphanumeric(20);

        if (userRecoveryTokenExist(recoveryToken)) {
            return generateRecoveryToken();
        } else {
            return recoveryToken;
        }
    }
}
