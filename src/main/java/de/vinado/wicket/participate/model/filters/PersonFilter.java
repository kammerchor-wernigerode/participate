package de.vinado.wicket.participate.model.filters;

import de.vinado.wicket.participate.model.Person;
import lombok.Getter;
import lombok.Setter;
import org.apache.wicket.util.string.Strings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
@Getter
@Setter
public class PersonFilter implements Serializable, IFilter<Person> {

    private String name;
    private String email;

    @Override
    public List<Person> filter(final List<Person> list) {
        final List<Person> result = new ArrayList<>();
        for (Person person : list) {
            if (validate(person.getSearchName(), getName())) {
                continue;
            }

            if (validate(person.getEmail(), getEmail())) {
                continue;
            }

            result.add(person);
        }

        return result;
    }

    @Override
    public boolean validate(final String str1, final String str2) {
        return !Strings.isEmpty(str2) && !str1.toLowerCase().contains(str2.toLowerCase());
    }
}
