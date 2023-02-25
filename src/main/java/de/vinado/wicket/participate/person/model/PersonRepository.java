package de.vinado.wicket.participate.person.model;

import de.vinado.wicket.participate.model.Person;

import java.util.stream.Stream;

public interface PersonRepository {

    Stream<Person> listInactivePersons();
}
