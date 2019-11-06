package de.vinado.wicket.participate.providers;

import de.vinado.wicket.participate.model.Person;
import de.vinado.wicket.participate.services.PersonService;
import lombok.RequiredArgsConstructor;
import org.wicketstuff.select2.ChoiceProvider;
import org.wicketstuff.select2.Response;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Provides a list of persons for a substring of their {@code searchName}.
 *
 * @author Vincent Nadoll
 */
@RequiredArgsConstructor
public class Select2PersonProvider extends ChoiceProvider<Person> {

    private final PersonService personService;

    @Override
    public String getDisplayValue(Person person) {
        return person.getSearchName();
    }

    @Override
    public String getIdValue(Person person) {
        return person.getId().toString();
    }

    @Override
    public void query(String term, int page, Response<Person> response) {
        response.addAll(personService.findPersons(term));
        response.setHasMore(false);
    }

    @Override
    public Collection<Person> toChoices(Collection<String> ids) {
        return ids.stream()
            .map(Long::parseLong)
            .map(personService::retrievePerson)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }
}
