package de.vinado.wicket.participate.components.tables;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.table.sort.BootstrapOrderByBorder;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.sort.AjaxOrderByLink;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortStateLocator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByLink;

public abstract class BootstrapAjaxOrderByBorder<S> extends BootstrapOrderByBorder<S> {

    public BootstrapAjaxOrderByBorder(String id, S property, ISortStateLocator<S> stateLocator) {
        super(id, property, stateLocator);
    }

    @Override
    protected OrderByLink<S> newOrderByLink(String id, S property, ISortStateLocator<S> stateLocator) {
        return new AjaxOrderByLink<S>(id, property, stateLocator) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                BootstrapAjaxOrderByBorder.this.onAjaxClick(target);
            }

            @Override
            protected void onSortChanged() {
                BootstrapAjaxOrderByBorder.this.onSortChanged();
            }

            @Override
            protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
                BootstrapAjaxOrderByBorder.this.updateAjaxAttributes(attributes);
            }
        };
    }

    protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
    }

    protected void onSortChanged() {
    }

    protected abstract void onAjaxClick(AjaxRequestTarget target);
}
