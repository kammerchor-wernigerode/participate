package de.vinado.wicket.repeater.table;

import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.danekja.java.util.function.serializable.SerializableFunction;
import org.danekja.java.util.function.serializable.SerializablePredicate;

import java.util.Comparator;
import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * @author Vincent Nadoll
 */
public abstract class FunctionalDataProvider<T> extends SortableDataProvider<T, SerializableFunction<T, ?>> {

    private static final long serialVersionUID = 2983444954650161772L;

    @Override
    public Iterator<? extends T> iterator(long first, long count) {
        return load()
            .filter(filter())
            .sorted(Comparator.comparing(keyExtractor(), keyComparator()))
            .skip(first).limit(count)
            .iterator();
    }

    protected abstract Stream<T> load();

    protected SerializablePredicate<? super T> filter() {
        return self -> true;
    }

    protected Function<T, String> keyExtractor() {
        return getSort().getProperty().andThen(FunctionalDataProvider::toString);
    }

    protected Comparator<String> keyComparator() {
        Comparator<String> comparator = getSort().isAscending()
            ? Comparator.naturalOrder()
            : Comparator.reverseOrder();
        return Comparator.nullsFirst(comparator);
    }

    private static String toString(Object property) {
        return null == property ? null : property.toString();
    }

    @Override
    public long size() {
        return load()
            .filter(filter())
            .count();
    }
}
