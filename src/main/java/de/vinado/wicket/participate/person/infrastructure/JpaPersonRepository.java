package de.vinado.wicket.participate.person.infrastructure;

import de.vinado.wicket.participate.model.Person;
import de.vinado.wicket.participate.model.Singer;
import de.vinado.wicket.participate.person.model.PersonRepository;
import de.vinado.wicket.participate.services.PersonService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Stream;

import static java.util.function.Predicate.not;

@Repository
@RequiredArgsConstructor
class JpaPersonRepository implements PersonRepository {

    private final PersonService personService;
    private final PersonRecordRepository recordRepository;

    @Override
    public Stream<Person> listInactivePersons() {
        return personService.listAllSingers()
            .filter(not(Singer::isActive))
            .map(Person.class::cast);
    }

    @Transactional
    @Override
    public void restore(@NonNull Person person) {
        recordRepository.restore(person);
    }
}
