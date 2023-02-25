package de.vinado.wicket.participate.person.infrastructure;

import de.vinado.wicket.participate.model.Person;
import de.vinado.wicket.participate.model.Singer;
import de.vinado.wicket.participate.person.model.PersonRepository;
import de.vinado.wicket.participate.services.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.stream.Stream;

import static java.util.function.Predicate.not;

@Repository
@RequiredArgsConstructor
class AntiCorruptingPersonRepository implements PersonRepository {

    private final PersonService personService;

    @Override
    public Stream<Person> listInactivePersons() {
        return personService.listAllSingers()
            .filter(not(Singer::isActive))
            .map(Person.class::cast);
    }
}
