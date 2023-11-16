package de.vinado.wicket.participate.services;

import de.vinado.wicket.participate.model.Person;
import de.vinado.wicket.participate.model.User;
import de.vinado.wicket.participate.model.dtos.AddUserDTO;

import java.util.List;

public interface UserService {

    User createUser(AddUserDTO dto);

    User saveUser(AddUserDTO dto);

    User assignPerson(AddUserDTO dto);

    List<User> getUsers();

    User getUser(String username);

    User getUser(Person person);

    boolean hasUser(String username);

    boolean hasUser(Person person);

    boolean hasUserRecoveryToken(String token);

    boolean startPasswordReset(String usernameOrEmail, boolean initial);

    boolean finishPasswordReset(String recoveryToken, String newPlainPassword);

    List<User> getAll();
}
