package de.vinado.wicket.participate.components.tables;

import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconType;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome6IconType;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackHeadersToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortStateLocator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.markup.html.WebMarkupContainer;

public class BootstrapAjaxHeadersToolbar<S> extends AjaxFallbackHeadersToolbar<S> {

    public BootstrapAjaxHeadersToolbar(DataTable<?, S> table, ISortStateLocator<S> stateLocator) {
        super(table, stateLocator);
    }

    @Override
    protected WebMarkupContainer newSortableHeader(String headerId, S property, ISortStateLocator<S> locator) {
        return new BootstrapAjaxOrderByBorder<S>(headerId, property, locator) {
            @Override
            protected IconType ascendingIconType() {
                return FontAwesome6IconType.sort_up_s;
            }

            @Override
            protected IconType descendingIconType() {
                return FontAwesome6IconType.sort_down_s;
            }

            @Override
            protected IconType unsortedIconType() {
                return FontAwesome6IconType.sort_s;
            }

            @Override
            protected void onAjaxClick(AjaxRequestTarget target) {
                target.add(getTable());
            }

            @Override
            protected void onSortChanged() {
                super.onSortChanged();
                getTable().setCurrentPage(0);
            }
        };
    }
}
