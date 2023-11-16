package de.vinado.wicket.participate.wicket.common;

import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;
import org.springframework.data.util.Streamable;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;

public interface PageRegistry extends Streamable<PageRegistrar> {

    default Stream<String> getPaths() {
        return get()
            .map(PageRegistrar::getPath);
    }

    default void mountPages(WebApplication application) {
        get().forEach(unwrapped(application::mountPage));
    }

    static Consumer<PageRegistrar> unwrapped(BiConsumer<String, Class<? extends Page>> action) {
        return registrar -> action.accept(registrar.getPath(), registrar.getPageClass());
    }
}
