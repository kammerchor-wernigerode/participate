package de.vinado.wicket.participate.ui.event;

import de.vinado.wicket.participate.model.filters.EventFilter;
import lombok.RequiredArgsConstructor;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.danekja.java.util.function.serializable.SerializableFunction;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class EventDataProvider extends SortableDataProvider<SelectableEventDetails, SerializableFunction<SelectableEventDetails, ?>> {

    private final IModel<List<SelectableEventDetails>> model;
    private final IModel<EventFilter> filter;

    @Override
    public Iterator<SelectableEventDetails> iterator(long first, long count) {
        return list()
            .sorted(Comparator.comparing(keyExtractor(), keyComparator()))
            .skip(first).limit(count)
            .iterator();
    }

    private Function<SelectableEventDetails, String> keyExtractor() {
        return getSort().getProperty().andThen(EventDataProvider::toString);
    }

    private Comparator<String> keyComparator() {
        Comparator<String> comparator = getSort().isAscending()
            ? Comparator.naturalOrder()
            : Comparator.reverseOrder();
        return Comparator.nullsFirst(comparator);
    }

    @Override
    public long size() {
        return list().count();
    }

    private Stream<SelectableEventDetails> list() {
        return model.getObject().stream()
            .filter(filter.getObject());
    }

    private static String toString(Object property) {
        return null == property ? null : property.toString();
    }

    @Override
    public IModel<SelectableEventDetails> model(SelectableEventDetails event) {
        return () -> event;
    }

    @Override
    public void detach() {
        filter.detach();
        super.detach();
    }
}
