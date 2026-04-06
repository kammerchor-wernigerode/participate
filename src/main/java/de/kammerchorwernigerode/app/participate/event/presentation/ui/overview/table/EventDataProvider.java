package de.kammerchorwernigerode.app.participate.event.presentation.ui.overview.table;

import de.kammerchorwernigerode.app.participate.event.presentation.model.EventEntry;
import de.kammerchorwernigerode.app.participate.event.presentation.model.EventEntryRepository;
import de.kammerchorwernigerode.app.participate.event.presentation.model.EventEntrySpecification;
import de.kammerchorwernigerode.app.participate.event.presentation.model.EventEntry_;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.repeater.util.JpaDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.danekja.java.util.function.serializable.SerializableFunction;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;

import java.util.Objects;

public class EventDataProvider extends JpaDataProvider<EventEntry> {

    public EventDataProvider(EventEntryRepository eventEntryRepository,
                             IModel<EventEntrySpecification> filterState) {
        super(eventEntryRepository, filterState.map(SerializableFunction.identity()));
    }

    @Override
    public Sort getSort() {
        Sort primary = super.getSort();
        Sort secondary = JpaSort.of(EventEntry_.startInstant, EventEntry_.endInstant);
        return primary.and(secondary);
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
