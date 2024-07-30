package de.vinado.wicket.participate.providers;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.apache.logging.log4j.util.Supplier;
import org.wicketstuff.select2.Response;
import org.wicketstuff.select2.StringTextChoiceProvider;

import java.util.Collection;

@RequiredArgsConstructor
public class Select2StringProvider extends StringTextChoiceProvider {

    private transient final Supplier<Collection<String>> choiceProvider;

    @Override
    public void query(String term, int page, Response<String> response) {
        Collection<String> choices = choiceProvider.get();

        if (Strings.isEmpty(term)) {
            response.addAll(choices);
            return;
        }

        choices.stream()
            .filter(Strings::isNotBlank)
            .filter(s -> s.toLowerCase().startsWith(term.toLowerCase()))
            .filter(s -> !s.equalsIgnoreCase(term))
            .forEach(response::add);

        response.add(term);
    }
}
