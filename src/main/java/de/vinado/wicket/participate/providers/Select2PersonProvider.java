package de.vinado.wicket.participate.providers;

import de.vinado.wicket.participate.data.Person;
import de.vinado.wicket.participate.service.PersonService;
import org.wicketstuff.select2.ChoiceProvider;
import org.wicketstuff.select2.Response;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class Select2PersonProvider extends ChoiceProvider<Person> {

    private PersonService personService;

    public Select2PersonProvider(final PersonService personService) {
        this.personService = personService;
    }

    @Override
    public String getDisplayValue(final Person person) {
        return person.getSearchName();
    }

    @Override
    public String getIdValue(final Person person) {
        return person.getId().toString();
    }

    @Override
    public void query(final String term, final int page, final Response<Person> response) {
        response.addAll(personService.findPersons("%" + term + "%"));
        response.setHasMore(false);
    }

    @Override
    public Collection<Person> toChoices(final Collection<String> ids) {
        final ArrayList<Person> personList = new ArrayList<>();
        for (String id : ids) {
            personList.add(personService.getPerson(Long.parseLong(id)));
        }
        return personList;
    }
}
