package de.vinado.wicket.participate.component.table;

import de.agilecoders.wicket.core.markup.html.bootstrap.navigation.BootstrapPagingNavigator;
import de.agilecoders.wicket.core.markup.html.bootstrap.navigation.BootstrapPagingNavigator.Size;
import de.agilecoders.wicket.core.markup.html.bootstrap.navigation.ajax.BootstrapAjaxPagingNavigator;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.table.toolbars.BootstrapNavigationToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class BootstrapAjaxNavigationToolbar extends BootstrapNavigationToolbar {


    public BootstrapAjaxNavigationToolbar(final DataTable<?, ?> table) {
        super(table);
    }

    public BootstrapAjaxNavigationToolbar(final DataTable<?, ?> table, Size size) {
        super(table, size);
    }

    @Override
    protected BootstrapPagingNavigator newPagingNavigator(final String navigatorId,
                                                          final DataTable<?, ?> table, final Size size) {
        return new BootstrapAjaxPagingNavigator(navigatorId, table) {
            @Override
            public Size getSize() {
                return size;
            }
        };
    }
}
