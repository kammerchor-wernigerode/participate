package de.vinado.wicket.participate.components.tables;

import de.agilecoders.wicket.core.markup.html.bootstrap.navigation.BootstrapPagingNavigator;
import de.agilecoders.wicket.core.markup.html.bootstrap.navigation.BootstrapPagingNavigator.Size;
import de.agilecoders.wicket.core.markup.html.bootstrap.navigation.ajax.BootstrapAjaxPagingNavigator;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.table.toolbars.BootstrapNavigationToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;

public class BootstrapAjaxNavigationToolbar extends BootstrapNavigationToolbar {


    public BootstrapAjaxNavigationToolbar(DataTable<?, ?> table, Size size) {
        super(table, size);
    }

    @Override
    protected BootstrapPagingNavigator newPagingNavigator(String navigatorId,
                                                          DataTable<?, ?> table, Size size) {
        return new BootstrapAjaxPagingNavigator(navigatorId, table) {
            @Override
            public Size getSize() {
                return size;
            }
        };
    }
}
