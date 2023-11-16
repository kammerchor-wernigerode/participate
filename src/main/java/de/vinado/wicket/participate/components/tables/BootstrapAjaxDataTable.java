package de.vinado.wicket.participate.components.tables;

import de.agilecoders.wicket.core.markup.html.bootstrap.navigation.BootstrapPagingNavigator;
import de.agilecoders.wicket.core.markup.html.bootstrap.table.TableBehavior;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.extensions.markup.html.repeater.data.table.NoRecordsToolbar;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.OddEvenItem;
import org.apache.wicket.model.IModel;

import java.util.List;

public class BootstrapAjaxDataTable<T, S> extends DataTable<T, S> {

    private final TableBehavior tableBehavior;

    public BootstrapAjaxDataTable(String id, List<? extends IColumn<T, S>> iColumns,
                                  ISortableDataProvider<T, S> dataProvider, int rowsPerPage) {
        super(id, iColumns, dataProvider, rowsPerPage);

        add(tableBehavior = new TableBehavior());

//        addTopToolbar(new BootstrapAjaxNavigationToolbar(this));
        addTopToolbar(new BootstrapAjaxHeadersToolbar<S>(this, dataProvider));
        addBottomToolbar(new BootstrapAjaxNavigationToolbar(this, BootstrapPagingNavigator.Size.Small));
        addBottomToolbar(new NoRecordsToolbar(this));
    }

    @Override
    protected Item<T> newRowItem(String id, int index, IModel<T> model) {
        return new OddEvenItem<>(id, index, model);
    }

    public BootstrapAjaxDataTable striped() {
        tableBehavior.striped();
        return this;
    }

    public BootstrapAjaxDataTable condensed() {
        tableBehavior.sm();
        return this;
    }

    public BootstrapAjaxDataTable bordered() {
        tableBehavior.bordered();
        return this;
    }

    public BootstrapAjaxDataTable hover() {
        tableBehavior.hover();
        return this;
    }
}
