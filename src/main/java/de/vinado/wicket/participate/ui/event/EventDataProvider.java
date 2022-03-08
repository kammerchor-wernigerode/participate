package de.vinado.wicket.participate.ui.event;

import de.vinado.wicket.participate.model.EventDetails;
import de.vinado.wicket.participate.model.filters.EventFilter;
import de.vinado.wicket.participate.services.EventService;
import lombok.RequiredArgsConstructor;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.danekja.java.util.function.serializable.SerializableFunction;

import java.util.Comparator;
import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class EventDataProvider extends SortableDataProvider<EventDetails, SerializableFunction<EventDetails, ?>> {

    private final IModel<EventFilter> filterModel;
    private final EventService eventService;

    @Override
    public Iterator<? extends EventDetails> iterator(long first, long count) {
        return streamFilteredParticipants()
            .sorted(Comparator.comparing(keyExtractor(), comparator()))
            .skip(first).limit(count)
            .iterator();
    }

    private Stream<EventDetails> streamFilteredParticipants() {
        return streamPreFilteredEvents()
            .filter(filterModel.getObject());
    }

    private Function<EventDetails, String> keyExtractor() {
        return getSort().getProperty().andThen(EventDataProvider::toString);
    }

    private static String toString(Object property) {
        return null == property ? null : property.toString();
    }

    private Comparator<String> comparator() {
        Comparator<String> comparator = getSort().isAscending()
            ? Comparator.naturalOrder()
            : Comparator.reverseOrder();
        return Comparator.nullsFirst(comparator);
    }

    @Override
    public long size() {
        return streamPreFilteredEvents().count();
    }

    private Stream<EventDetails> streamPreFilteredEvents() {
        return filterModel.getObject().isShowAll()
            ? eventService.listAll()
            : eventService.getUpcomingEventDetails().stream();
    }

    @Override
    public IModel<EventDetails> model(EventDetails event) {
        return () -> event;
    }

    @Override
    public void detach() {
        filterModel.detach();
        super.detach();
    }
}
