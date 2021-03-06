package de.vinado.wicket.participate.components.tables.columns;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconType;
import de.vinado.wicket.participate.components.panels.BootstrapAjaxLinkPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public abstract class BootstrapAjaxLinkColumn<T, S> extends AbstractColumn<T, S> {

    private IconType iconType;

    private IModel<String> tooltipModel;

    public BootstrapAjaxLinkColumn(final IconType iconType) {
        this(iconType, null);
    }

    public BootstrapAjaxLinkColumn(final IconType iconType, final IModel<String> tooltipModel) {
        super(Model.of(""));

        this.iconType = iconType;
        this.tooltipModel = tooltipModel;
    }

    @Override
    public void populateItem(final Item<ICellPopulator<T>> cellItem, final String componentId, final IModel<T> rowModel) {
        final BootstrapAjaxLinkPanel link = new BootstrapAjaxLinkPanel(componentId, Buttons.Type.Link, iconType, tooltipModel) {
            @Override
            public void onClick(final AjaxRequestTarget target) {
                BootstrapAjaxLinkColumn.this.onClick(target, rowModel);
            }
        };
        cellItem.add(link);
    }

    @Override
    public String getCssClass() {
        return "width-fix-30";
    }

    public abstract void onClick(final AjaxRequestTarget target, final IModel<T> rowModel);
}
