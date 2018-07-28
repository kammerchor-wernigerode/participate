package de.vinado.wicket.participate.data.filters;

import de.vinado.wicket.participate.data.Person;
import org.apache.wicket.util.string.Strings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
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

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }
}
