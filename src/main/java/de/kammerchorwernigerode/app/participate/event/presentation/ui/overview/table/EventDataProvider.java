package de.kammerchorwernigerode.app.participate.event.presentation.ui.overview.table;

import de.kammerchorwernigerode.app.participate.data.domain.OffsetPageRequest;
import de.kammerchorwernigerode.app.participate.event.presentation.model.EventEntry;
import de.kammerchorwernigerode.app.participate.event.presentation.model.EventEntryRepository;
import de.kammerchorwernigerode.app.participate.event.presentation.model.EventEntry_;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.repeater.data.sort.OrderState;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.repeater.util.SpringDataProvider;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.IFilterStateLocator;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.util.Streamable;

import java.util.Objects;

import lombok.Getter;

public class EventDataProvider extends SpringDataProvider<EventEntry>
    implements ISortableDataProvider<EventEntry, String> {

    @Getter
    private final OrderState sortState = new OrderState();

    private final IFilterStateLocator<? extends Specification<EventEntry>> filterStateLocator;
    private final EventEntryRepository eventEntryRepository;

    public EventDataProvider(IFilterStateLocator<? extends Specification<EventEntry>> filterStateLocator,
                             EventEntryRepository eventEntryRepository) {
        this.filterStateLocator = filterStateLocator;
        this.eventEntryRepository = eventEntryRepository;
    }

    @Override
    public Streamable<EventEntry> streamable(long offset, long pageSize) {
        Specification<EventEntry> spec = filterStateLocator.getFilterState();
        Pageable pageable = createPageable(offset, pageSize);
        return eventEntryRepository.findBy(spec, query -> query
            .slice(pageable));
    }

    private Pageable createPageable(long offset, long pageSize) {
        Sort sort = getSort();
        return new OffsetPageRequest(offset, pageSize, sort);
    }

    public Sort getSort() {
        Sort primary = sortState.getOrder()
            .map(Sort::by)
            .orElseGet(Sort::unsorted);
        Sort secondary = JpaSort.of(EventEntry_.startInstant, EventEntry_.endInstant);
        return primary.and(secondary);
    }

    public void setOrder(@NonNull String property, @NonNull SortOrder sortOrder) {
        sortState.setPropertySortOrder(property, sortOrder);
    }

    @Override
    public long size() {
        Specification<EventEntry> spec = filterStateLocator.getFilterState();
        return eventEntryRepository.count(spec);
    }

    @Override
    public IModel<EventEntry> model(EventEntry entry) {
        return new EventEntryModel(entry);
    }


    private static class EventEntryModel extends Model<EventEntry> {

        public EventEntryModel(EventEntry entry) {
            super(entry);
        }

        @Override
        public int hashCode() {
            EventEntry entry = getObject();
            return Objects.hash(
                entry.getSummary(),
                entry.getStartInstant(),
                entry.getStartZoneId(),
                entry.getEndInstant(),
                entry.getEndZoneId(),
                entry.getLocation(),
                entry.getAccepted(),
                entry.getDeclined(),
                entry.getDeclined());
        }
    }
}
