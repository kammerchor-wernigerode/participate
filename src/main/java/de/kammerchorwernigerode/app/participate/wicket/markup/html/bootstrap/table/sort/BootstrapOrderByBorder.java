package de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.table.sort;

import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.icon.Bi;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.image.IconBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.sort.AjaxOrderByLink;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortState;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortStateLocator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.border.Border;

public abstract class BootstrapOrderByBorder<S> extends Border {

    private final S property;
    private final ISortStateLocator<S> stateLocator;

    public BootstrapOrderByBorder(String id, S property, ISortStateLocator<S> stateLocator) {
        super(id);
        this.property = property;
        this.stateLocator = stateLocator;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        BootstrapOrderByLink link = new BootstrapOrderByLink("link", property, stateLocator);
        addToBorder(link);

        WebMarkupContainer icon = new WebMarkupContainer("icon");
        icon.setVisible(false);
        link.add(icon);

        ISortState<S> sortState = stateLocator.getSortState();
        SortOrder sortOrder = sortState.getPropertySortOrder(property);

        if (SortOrder.ASCENDING.equals(sortOrder)) {
            icon.setVisible(true);
            icon.add(new IconBehavior(Bi.chevron_double_up));
        } else if (SortOrder.DESCENDING.equals(sortOrder)) {
            icon.setVisible(true);
            icon.add(new IconBehavior(Bi.chevron_double_down));
        } else {
            icon.setVisible(true);
            icon.add(new IconBehavior(Bi.chevron_expand));
        }
    }

    protected abstract void onClick(AjaxRequestTarget target);

    protected abstract void onSortChanged();


    private class BootstrapOrderByLink extends AjaxOrderByLink<S> {

        public BootstrapOrderByLink(String id, S sortProperty, ISortStateLocator<S> stateLocator) {
            super(id, sortProperty, stateLocator);
        }

        @Override
        public void onClick(AjaxRequestTarget target) {
            BootstrapOrderByBorder.this.onClick(target);
        }

        @Override
        protected void onSortChanged() {
            BootstrapOrderByBorder.this.onSortChanged();
        }
    }
}
