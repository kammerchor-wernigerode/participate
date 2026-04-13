package de.kammerchorwernigerode.app.participate.person.presentation.ui.overview;

import de.kammerchorwernigerode.app.participate.person.presentation.model.PersonEntry;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.table.BootstrapDataTable;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.Session;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;

import java.util.List;

public class PersonTable extends BootstrapDataTable<PersonEntry, String[]> {

    public static final MetaDataKey<Long> personTablePageSize = new MetaDataKey<>() { };

    public PersonTable(String id, List<? extends IColumn<PersonEntry, String[]>> columns,
                       ISortableDataProvider<PersonEntry, String[]> dataProvider, int rowsPerPage) {
        super(id, columns, dataProvider, rowsPerPage);
    }

    @Override
    protected void onPageSizeChanged() {
        Session session = Session.get();
        long rowsPerPage = getItemsPerPage();
        session.setMetaData(personTablePageSize, rowsPerPage);
    }
}
