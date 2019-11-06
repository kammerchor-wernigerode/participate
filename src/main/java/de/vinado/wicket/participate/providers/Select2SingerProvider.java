package de.vinado.wicket.participate.providers;

import de.vinado.wicket.participate.model.Singer;
import de.vinado.wicket.participate.services.PersonService;
import lombok.RequiredArgsConstructor;
import org.wicketstuff.select2.ChoiceProvider;
import org.wicketstuff.select2.Response;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Provides a list of singers for a substring of their {@code searchName}.
 *
 * @author Vincent Nadoll
 */
@RequiredArgsConstructor
public class Select2SingerProvider extends ChoiceProvider<Singer> {

    private final PersonService personService;

    @Override
    public String getDisplayValue(Singer singer) {
        return singer.getSearchName();
    }

    @Override
    public String getIdValue(Singer singer) {
        return singer.getId().toString();
    }

    @Override
    public void query(String term, int page, Response<Singer> response) {
        response.addAll(personService.findSingers(term));
        response.setHasMore(false);
    }

    @Override
    public Collection<Singer> toChoices(Collection<String> ids) {
        return ids.stream()
            .map(Long::parseLong)
            .map(personService::retrieveSinger)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }
}
