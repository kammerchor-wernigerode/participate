package de.vinado.wicket.participate.providers;

import de.vinado.wicket.participate.email.InternetAddressFactory;
import de.vinado.wicket.participate.services.PersonService;
import lombok.RequiredArgsConstructor;
import org.wicketstuff.select2.ChoiceProvider;
import org.wicketstuff.select2.Response;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.mail.internet.InternetAddress;

/**
 * Provides a list of persons for a substring of their {@code email} address.
 *
 * @author Vincent Nadoll
 */
@RequiredArgsConstructor
public class Select2EmailAddressProvider extends ChoiceProvider<InternetAddress> {

    private static final String UTF_8 = StandardCharsets.UTF_8.name();

    private final PersonService personService;

    @Override
    public String getDisplayValue(InternetAddress address) {
        return address.toString();
    }

    @Override
    public String getIdValue(InternetAddress address) {
        return address.getAddress();
    }

    @Override
    public void query(String term, int page, Response<InternetAddress> response) {
        personService.findPersons(term).stream()
            .map(InternetAddressFactory::create)
            .forEach(response::add);
        response.setHasMore(false);
    }

    @Override
    public Collection<InternetAddress> toChoices(Collection<String> addresses) {
        return addresses.stream()
            .map(personService::getPerson)
            .filter(Objects::nonNull)
            .map(InternetAddressFactory::create)
            .collect(Collectors.toList());
    }
}
