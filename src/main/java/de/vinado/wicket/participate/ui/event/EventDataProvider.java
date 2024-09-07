package de.vinado.wicket.participate.ui.event;

import de.vinado.wicket.participate.model.filters.EventFilter;
import de.vinado.wicket.participate.services.EventService;
import de.vinado.wicket.repeater.table.FilterableDataProvider;
import org.apache.wicket.model.IModel;

import java.util.stream.Stream;

public class EventDataProvider extends FilterableDataProvider<SelectableEventDetails> {

    private final IModel<EventFilter> filterModel;
    private final EventService eventService;

    public EventDataProvider(IModel<EventFilter> filter, EventService eventService) {
        super(filter);
        this.filterModel = filter;
        this.eventService = eventService;
    }

    @Override
    protected Stream<SelectableEventDetails> load() {
        return (filterModel.getObject().isShowAll()
            ? eventService.listAll()
            : eventService.getUpcomingEventDetails().stream())
            .map(SelectableEventDetails::new);
    }

    @Override
    public IModel<SelectableEventDetails> model(SelectableEventDetails event) {
        return () -> event;
    }
}
