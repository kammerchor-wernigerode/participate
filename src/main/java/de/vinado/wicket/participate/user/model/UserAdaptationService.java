package de.vinado.wicket.participate.user.model;

import de.vinado.wicket.participate.model.Person;
import de.vinado.wicket.participate.model.User;

import java.util.Optional;

public interface UserAdaptationService {

    Optional<Person> getPerson(User user);

    Optional<User> getUser(Person person);
}
