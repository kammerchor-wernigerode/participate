package de.vinado.wicket.participate.person.model;

import de.vinado.wicket.participate.model.Person;
import lombok.NonNull;

import java.util.stream.Stream;

public interface PersonRepository {

    Stream<Person> listInactivePersons();

    void restore(@NonNull Person person);
}
