package de.vinado.wicket.participate.component.provider;

import de.vinado.wicket.participate.data.Singer;
import de.vinado.wicket.participate.service.PersonService;
import org.wicketstuff.select2.ChoiceProvider;
import org.wicketstuff.select2.Response;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class Select2SingerProvider extends ChoiceProvider<Singer> {

    private PersonService personService;

    public Select2SingerProvider(final PersonService personService) {
        this.personService = personService;
    }

    @Override
    public String getDisplayValue(final Singer singer) {
        return singer.getSearchName();
    }

    @Override
    public String getIdValue(final Singer singer) {
        return singer.getId().toString();
    }

    @Override
    public void query(final String term, final int page, final Response<Singer> response) {
        response.addAll(personService.findSingers("%" + term + "%"));
        response.setHasMore(false);
    }

    @Override
    public Collection<Singer> toChoices(final Collection<String> ids) {
        final ArrayList<Singer> singerList = new ArrayList<>();
        for (String id : ids) {
            singerList.add(personService.getSinger(Long.parseLong(id)));
        }
        return singerList;
    }
}
