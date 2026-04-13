package de.kammerchorwernigerode.app.participate.event.presentation.ui.overview.table;

import de.kammerchorwernigerode.app.participate.event.presentation.model.EventEntry;
import de.kammerchorwernigerode.app.participate.event.presentation.model.EventSelected;
import de.kammerchorwernigerode.app.participate.wicket.behavior.UpdateOnEventBehavior;
import de.kammerchorwernigerode.app.participate.wicket.management.ManagementWicketSession;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.table.BootstrapDataTable;
import org.apache.wicket.ClassAttributeModifier;
import org.apache.wicket.Session;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.OddEvenItem;
import org.apache.wicket.model.IModel;

import java.util.List;
import java.util.function.Predicate;

public class EventTable extends BootstrapDataTable<EventEntry, String> {

    public EventTable(String id, List<? extends IColumn<EventEntry, String>> columns,
                      ISortableDataProvider<EventEntry, String> dataProvider, int rowsPerPage) {
        super(id, columns, dataProvider, rowsPerPage);
    }

    @Override
    protected Item<EventEntry> newRowItem(String id, int index, IModel<EventEntry> model) {
        Item<EventEntry> item = new HighlightSelectedItem(id, index, model);
        item.add(new UpdateOnEventBehavior<>(EventSelected.class));
        return item;
    }


    private static class HighlightSelectedItem extends OddEvenItem<EventEntry> {

        private static final AttributeAppender HIGHLIGHT_BEHAVIOR =
            ClassAttributeModifier.append("class", "table-primary");

        public HighlightSelectedItem(String id, int index, IModel<EventEntry> model) {
            super(id, index, model);
        }

        @Override
        protected void onConfigure() {
            super.onConfigure();

            Predicate<Behavior> isHighlighted = HIGHLIGHT_BEHAVIOR::equals;
            getBehaviors().stream()
                .filter(isHighlighted)
                .forEach(this::remove);

            Long itemEventId = getItemEventId();
            Long selectedEventId = getSelectedEventId();

            if (itemEventId.equals(selectedEventId)) {
                add(HIGHLIGHT_BEHAVIOR);
            }
        }

        private Long getItemEventId() {
            EventEntry entry = getModelObject();
            return entry.getId();
        }

        private Long getSelectedEventId() {
            Session session = Session.get();
            return session.getMetaData(ManagementWicketSession.selectedEventId);
        }
    }
}
