package de.vinado.wicket.participate.wicket.form.app;

import de.vinado.wicket.participate.wicket.common.PageRegistrar;
import de.vinado.wicket.participate.wicket.common.PageRegistry;
import de.vinado.wicket.participate.wicket.form.ui.FormPage;
import de.vinado.wicket.participate.wicket.form.ui.FormSignInPage;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.wicket.Page;
import org.springframework.lang.NonNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FormPageRegistry implements PageRegistry {

    private static final Map<String, Class<? extends Page>> storage;

    static {
        storage = new HashMap<>();

        storage.put("/participant", FormPage.class);
        storage.put("/login", FormSignInPage.class);
    }

    @NonNull
    @Override
    public Iterator<PageRegistrar> iterator() {
        return storage.entrySet().stream()
            .map(pairwise(PageRegistrar::new))
            .iterator();
    }

    private static <K, V, R> Function<Map.Entry<K, V>, R> pairwise(BiFunction<K, V, R> remapper) {
        return entry -> remapper.apply(entry.getKey(), entry.getValue());
    }

    public static FormPageRegistry instance() {
        return Holder.INSTANCE;
    }


    private static final class Holder {
        private static final FormPageRegistry INSTANCE = new FormPageRegistry();
    }
}
