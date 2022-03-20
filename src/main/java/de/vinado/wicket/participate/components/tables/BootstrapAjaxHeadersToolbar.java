package de.vinado.wicket.participate.components.tables;

import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconType;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome5IconType;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.table.AjaxFallbackHeadersToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortStateLocator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.markup.html.WebMarkupContainer;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class BootstrapAjaxHeadersToolbar<S> extends AjaxFallbackHeadersToolbar<S> {

    /**
     * Constructor
     *
     * @param table        data table this toolbar will be attached to
     * @param stateLocator
     */
    public BootstrapAjaxHeadersToolbar(final DataTable<?, S> table, final ISortStateLocator<S> stateLocator) {
        super(table, stateLocator);
    }

    @Override
    protected WebMarkupContainer newSortableHeader(final String headerId, final S property, final ISortStateLocator<S> locator) {
        return new BootstrapAjaxOrderByBorder<S>(headerId, property, locator) {
            @Override
            protected IconType ascendingIconType() {
                return FontAwesome5IconType.sort_up_s;
            }

            @Override
            protected IconType descendingIconType() {
                return FontAwesome5IconType.sort_down_s;
            }

            @Override
            protected IconType unsortedIconType() {
                return FontAwesome5IconType.sort_s;
            }

            @Override
            protected void onAjaxClick(final AjaxRequestTarget target) {
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
