package de.vinado.wicket.participate.components.tables.columns;

import de.agilecoders.wicket.core.markup.html.bootstrap.components.TooltipBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.components.TooltipConfig;
import de.vinado.wicket.participate.components.panels.LinkPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;

import java.time.Duration;

/**
 * @author Vincent Nadoll
 */
public abstract class AjaxLinkColumn<T> extends PropertyColumn<T, String> {

    private static final TooltipConfig TOOLTIP_CONFIG = new TooltipConfig()
        .withDelay(Duration.ofMillis(300))
        .withPlacement(TooltipConfig.Placement.bottom);

    public AjaxLinkColumn(IModel<String> displayModel, String sortProperty, String propertyExpression) {
        super(displayModel, sortProperty, propertyExpression);
    }

    public AjaxLinkColumn(IModel<String> displayModel, String propertyExpression) {
        super(displayModel, propertyExpression);
    }

    @Override
    public void populateItem(Item<ICellPopulator<T>> item, String componentId, IModel<T> rowModel) {
        LinkPanel container = new LinkPanel(componentId, getDataModel(rowModel), id -> {
            AjaxLink<String> link = new AjaxLink<>(id) {
                @Override
                public void onClick(AjaxRequestTarget target) {
                    AjaxLinkColumn.this.onClick(target, rowModel);
                }
            };
            link.add(newTooltip(rowModel));
            return link;
        });
        item.add(container);
    }

    protected abstract void onClick(AjaxRequestTarget target, IModel<T> rowModel);

    protected TooltipBehavior newTooltip(IModel<T> rowModel) {
        return new TooltipBehavior(getDataModel(rowModel).map(String::valueOf), TOOLTIP_CONFIG);
    }
}
