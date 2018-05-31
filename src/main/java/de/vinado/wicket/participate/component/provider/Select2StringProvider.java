package de.vinado.wicket.participate.component.provider;

import org.apache.wicket.util.string.Strings;
import org.wicketstuff.select2.Response;
import org.wicketstuff.select2.StringTextChoiceProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class Select2StringProvider extends StringTextChoiceProvider {

    private List<String> strings;

    public Select2StringProvider(final List<String> strings) {
        this.strings = strings;
    }

    @Override
    public void query(final String term, final int page, final Response<String> response) {
        if (Strings.isEmpty(term)) {
            response.addAll(this.strings);
            return;
        }

        final List<String> strings = new ArrayList<>();
        for (String string : this.strings) {
            if (string.toLowerCase().startsWith(term.toLowerCase()) && !string.equalsIgnoreCase(term)) {
                strings.add(string);
            }
        }
        strings.sort(String.CASE_INSENSITIVE_ORDER);
        response.addAll(strings);
        response.add(term);
    }
}
