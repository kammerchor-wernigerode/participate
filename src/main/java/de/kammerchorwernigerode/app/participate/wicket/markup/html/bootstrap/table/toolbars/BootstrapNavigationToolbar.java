package de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.table.toolbars;

import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.navigation.paging.BootstrapNavigatorLabel;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.navigation.paging.BootstrapPageSizeSelector;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.navigation.paging.BootstrapPagingNavigator;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.model.IModel;

import java.util.List;

public class BootstrapNavigationToolbar extends AbstractToolbar {

    public BootstrapNavigationToolbar(DataTable<?, ?> table) {
        super(table);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        DataTable<?, ?> table = getTable();

        WebMarkupContainer cell = new WebMarkupContainer("cell");
        cell.add(AttributeModifier.replace("colspan", colspan(table)));
        add(cell);

        BootstrapPageSizeSelector pageSizeSelector = new BootstrapPageSizeSelector("pageSizeSelector", table) {

            @Override
            protected void onPageSizeChanged() {
                BootstrapNavigationToolbar.this.onPageSizeChanged();
            }
        };
        pageSizeSelector.setRenderBodyOnly(true);
        cell.add(pageSizeSelector);

        Navigation navigation = new Navigation("navigation");
        cell.add(navigation);

        BootstrapPagingNavigator pagingNavigator = new BootstrapPagingNavigator("navigator", table);
        navigation.add(pagingNavigator);

        BootstrapNavigatorLabel navigatorLabel = new BootstrapNavigatorLabel("label", table);
        navigation.add(navigatorLabel);
    }

    private IModel<Integer> colspan(DataTable<?, ?> table) {
        return () -> {
            List<? extends IColumn<?, ?>> columns = table.getColumns();
            return columns.size();
        };
    }

    protected void onPageSizeChanged() {
    }


    private class Navigation extends WebMarkupContainer {

        public Navigation(String id) {
            super(id);
        }

        @Override
        protected void onConfigure() {
            super.onConfigure();

            DataTable<?, ?> table = getTable();
            long pageCount = table.getPageCount();
            setVisible(pageCount > 1);
        }
    }
}
