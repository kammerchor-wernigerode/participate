package de.vinado.wicket.participate.providers;

import de.vinado.wicket.participate.model.Person;
import de.vinado.wicket.participate.services.PersonService;
import lombok.extern.slf4j.Slf4j;
import org.wicketstuff.select2.ChoiceProvider;
import org.wicketstuff.select2.Response;

import javax.mail.internet.InternetAddress;
import javax.persistence.NoResultException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
@Slf4j
public class Select2EmailAddressProvider extends ChoiceProvider<InternetAddress> {

    private PersonService personService;

    public Select2EmailAddressProvider(final PersonService personService) {
        this.personService = personService;
    }

    @Override
    public String getDisplayValue(final InternetAddress address) {
        return address.toString();
    }

    @Override
    public String getIdValue(final InternetAddress address) {
        return address.getAddress();
    }

    @Override
    public void query(final String term, final int page, final Response<InternetAddress> response) {
        response.addAll(personService.findPersons("%" + term + "%").stream().map(this::newInternetAddress).collect(Collectors.toList()));
        response.setHasMore(false);
    }

    @Override
    public Collection<InternetAddress> toChoices(final Collection<String> addresses) {
        final ArrayList<Person> personList = new ArrayList<>();
        for (String email : addresses) {
            try {
                personList.add(personService.retrievePerson(email));
            } catch (NoResultException e) {
                log.debug("Could not find person w/ email={}", email);
            }
        }
        return personList.stream().map(this::newInternetAddress).collect(Collectors.toList());
    }

    private InternetAddress newInternetAddress(final Person person) {
        try {
            return new InternetAddress(person.getEmail(), person.getDisplayName(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return new InternetAddress();
        }
    }
}
