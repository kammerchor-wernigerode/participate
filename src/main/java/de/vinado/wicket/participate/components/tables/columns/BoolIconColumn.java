package de.vinado.wicket.participate.components.tables.columns;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome5IconType;
import de.vinado.wicket.participate.components.TextAlign;
import de.vinado.wicket.participate.components.panels.IconPanel;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public abstract class BoolIconColumn<T, S> extends PropertyColumn<T, S> {


    public BoolIconColumn(final IModel<String> displayModel, final S sortProperty, final String propertyExpression) {
        super(displayModel, sortProperty, propertyExpression);
    }

    public BoolIconColumn(final IModel<String> displayModel, final String propertyExpression) {
        super(displayModel, propertyExpression);
    }

    @Override
    public void populateItem(final Item<ICellPopulator<T>> item, final String componentId, final IModel<T> rowModel) {
        item.add(new IconPanel(componentId, getCondition(rowModel) ? FontAwesome5IconType.check_s : FontAwesome5IconType.times_s, IconPanel.Color.DEFAULT, TextAlign.CENTER));
    }

    public abstract boolean getCondition(final IModel<T> rowModel);
}
