package de.vinado.app.participate.wicket.management;

import de.vinado.app.participate.wicket.WicketConfiguration;
import de.vinado.app.participate.wicket.WicketProperties;
import de.vinado.wicket.participate.ManagementApplication;
import de.vinado.wicket.participate.ui.pages.ManagementPageRegistry;
import lombok.NonNull;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;
import java.util.stream.Stream;

@Configuration
public class ManagementConfiguration extends WicketConfiguration {

    static final String APP_ROOT = "/_";

    public ManagementConfiguration(@NonNull WicketProperties properties) {
        super(APP_ROOT, "wicket.participate.management", ManagementApplication.class, properties);
    }

    @Override
    protected String[] urlPatterns() {
        return Stream.concat(listResourceRoots(), listPagePaths())
            .map(prepend(APP_ROOT))
            .toArray(String[]::new);
    }

    private Stream<String> listResourceRoots() {
        return Stream.of("/wicket/*");
    }

    private Stream<String> listPagePaths() {
        return ManagementPageRegistry.getInstance().getPaths()
            .map(path -> path.replaceAll("#\\{.+\\}", "*"));
    }

    private static Function<String, String> prepend(String value) {
        return path -> value + path;
    }
}
