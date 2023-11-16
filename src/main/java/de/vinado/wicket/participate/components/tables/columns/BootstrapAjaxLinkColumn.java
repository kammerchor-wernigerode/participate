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

public abstract class BootstrapAjaxLinkColumn<T, S> extends AbstractColumn<T, S> {

    private IconType iconType;

    private IModel<String> tooltipModel;

    public BootstrapAjaxLinkColumn(IconType iconType) {
        this(iconType, null);
    }

    public BootstrapAjaxLinkColumn(IconType iconType, IModel<String> tooltipModel) {
        super(Model.of(""));

        this.iconType = iconType;
        this.tooltipModel = tooltipModel;
    }

    @Override
    public void populateItem(Item<ICellPopulator<T>> cellItem, String componentId, IModel<T> rowModel) {
        BootstrapAjaxLinkPanel link = new BootstrapAjaxLinkPanel(componentId, Buttons.Type.Link, iconType, tooltipModel) {
            @Override
            public void onClick(AjaxRequestTarget target) {
                BootstrapAjaxLinkColumn.this.onClick(target, rowModel);
            }
        };
        link.setRenderBodyOnly(true);
        cellItem.add(link);
    }

    @Override
    public String getCssClass() {
        return "width-fix-30";
    }

    public abstract void onClick(AjaxRequestTarget target, IModel<T> rowModel);
}
