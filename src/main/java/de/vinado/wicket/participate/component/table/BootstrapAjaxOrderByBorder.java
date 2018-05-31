package de.vinado.wicket.participate.component.table;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.table.sort.BootstrapOrderByBorder;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.extensions.ajax.markup.html.repeater.data.sort.AjaxFallbackOrderByLink;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortStateLocator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByLink;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public abstract class BootstrapAjaxOrderByBorder<S> extends BootstrapOrderByBorder<S> {


    /**
     * @param id           the component id
     * @param property     the property to be sorted on
     * @param stateLocator the state locator
     */
    public BootstrapAjaxOrderByBorder(String id, S property, ISortStateLocator<S> stateLocator) {
        super(id, property, stateLocator);
    }

    @Override
    protected OrderByLink<S> newOrderByLink(String id, S property, ISortStateLocator<S> stateLocator) {
        return new AjaxFallbackOrderByLink<S>(id, property, stateLocator) {
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

    protected abstract void onAjaxClick(final AjaxRequestTarget target);
}
