package de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.table.toolbars;

import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.table.sort.BootstrapOrderByBorder;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortStateLocator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.HeadersToolbar;
import org.apache.wicket.markup.html.WebMarkupContainer;

public class BootstrapHeadersToolbar<S> extends HeadersToolbar<S> {

    public <T> BootstrapHeadersToolbar(DataTable<T, S> table, ISortStateLocator<S> stateLocator) {
        super(table, stateLocator);
        table.setOutputMarkupId(true);
    }

    @Override
    protected WebMarkupContainer newSortableHeader(String headerId, S property, ISortStateLocator<S> locator) {
        return new SortableHeader(headerId, property, locator);
    }


    private class SortableHeader extends BootstrapOrderByBorder<S> {

        public SortableHeader(String id, S property, ISortStateLocator<S> stateLocator) {
            super(id, property, stateLocator);
        }

        @Override
        protected void onClick(AjaxRequestTarget target) {
            DataTable<?, ?> table = getTable();
            target.add(table);
        }

        @Override
        protected void onSortChanged() {
            DataTable<?, ?> table = getTable();
            table.setCurrentPage(0);
        }
    }
}
