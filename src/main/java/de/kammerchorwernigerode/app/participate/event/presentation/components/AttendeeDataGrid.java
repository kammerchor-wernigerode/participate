package de.kammerchorwernigerode.app.participate.event.presentation.components;

import de.kammerchorwernigerode.app.participate.event.presentation.model.details.attendee.AttendeeDetailsEntry;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.util.Attributes;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;

public class AttendeeDataGrid extends Panel {

    private final DataView<AttendeeDetailsEntry> dataView;

    protected AttendeeDataGrid(String id, IDataProvider<AttendeeDetailsEntry> dataProvider) {
        super(id);
        this.dataView = new AttendeeDataView("rows", dataProvider);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(dataView);
    }

    @Override
    protected void onComponentTag(ComponentTag tag) {
        super.onComponentTag(tag);

        Attributes.addClass(tag, "datagrid");
        tag.put("role", "table");
        tag.put("aria-rowcount", dataView.size());
    }


    private static class AttendeeDataView extends DataView<AttendeeDetailsEntry> {

        protected AttendeeDataView(String id, IDataProvider<AttendeeDetailsEntry> dataProvider) {
            super(id, dataProvider);
        }

        @Override
        protected void populateItem(Item<AttendeeDetailsEntry> item) {
        }
    }
}
