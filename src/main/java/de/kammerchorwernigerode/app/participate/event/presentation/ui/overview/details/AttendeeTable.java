package de.kammerchorwernigerode.app.participate.event.presentation.ui.overview.details;

import de.kammerchorwernigerode.app.participate.event.presentation.model.AttendeeEntry;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.table.BootstrapDataTable;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.Session;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;

import java.util.List;

public class AttendeeTable extends BootstrapDataTable<AttendeeEntry, String> {

    public static final MetaDataKey<Long> attendeeTablePageSize = new MetaDataKey<>() { };

    public AttendeeTable(String id, List<? extends IColumn<AttendeeEntry, String>> columns,
                         ISortableDataProvider<AttendeeEntry, String> dataProvider, int rowsPerPage) {
        super(id, columns, dataProvider, rowsPerPage);
    }

    @Override
    protected void onPageSizeChanged() {
        Session session = Session.get();
        long rowsPerPage = getItemsPerPage();
        session.setMetaData(attendeeTablePageSize, rowsPerPage);
    }
}
