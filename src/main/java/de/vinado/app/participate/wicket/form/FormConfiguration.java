package de.vinado.app.participate.wicket.form;

import de.vinado.app.participate.wicket.WicketConfiguration;
import de.vinado.app.participate.wicket.WicketProperties;
import de.vinado.wicket.participate.wicket.form.app.FormApplication;
import de.vinado.wicket.participate.wicket.form.app.FormPageRegistry;
import lombok.NonNull;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;
import java.util.stream.Stream;

import static java.util.stream.Stream.concat;

@Configuration
public class FormConfiguration extends WicketConfiguration {

    static final String APP_ROOT = "/form";

    public FormConfiguration(@NonNull WicketProperties properties) {
        super(APP_ROOT, "wicket.participate.form", FormApplication.class, properties);
    }

    @Override
    protected String[] urlPatterns() {
        return concat(listResourceRoots(), listPagePaths()).map(prepend(APP_ROOT)).toArray(String[]::new);
    }

    private Stream<String> listResourceRoots() {
        return Stream.of("/wicket/*");
    }

    private Stream<String> listPagePaths() {
        return FormPageRegistry.instance().getPaths();
    }

    private static Function<String, String> prepend(String value) {
        return path -> value + path;
    }
}
