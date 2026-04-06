package de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.table;

import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.table.toolbars.BootstrapHeadersToolbar;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackDefaultDataTable;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxNavigationToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.extensions.markup.html.repeater.data.table.NoRecordsToolbar;

import java.util.List;

public class BootstrapDataTable<T, S> extends AjaxFallbackDefaultDataTable<T, S> {

    public BootstrapDataTable(String id, List<? extends IColumn<T, S>> columns,
                              ISortableDataProvider<T, S> dataProvider, int rowsPerPage) {
        super(id, columns, dataProvider, rowsPerPage);
    }

    @Override
    protected void addToolBars(ISortableDataProvider<T, S> dataProvider) {
        addTopToolbar(new AjaxNavigationToolbar(this));
        addTopToolbar(new BootstrapHeadersToolbar<>(this, dataProvider));
        addBottomToolbar(new NoRecordsToolbar(this));
    }
}
