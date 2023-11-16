package de.vinado.wicket.participate.components.tables.columns;

import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.basic.EnumLabel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

public class EnumColumn<T, S, E extends Enum<E>> extends PropertyColumn<T, S> {

    public EnumColumn(IModel<String> displayModel, S sortProperty, String propertyExpression) {
        super(displayModel, sortProperty, propertyExpression);
    }

    public EnumColumn(IModel<String> displayModel, String propertyExpression) {
        super(displayModel, propertyExpression);
    }

    @Override
    public void populateItem(Item<ICellPopulator<T>> item, String componentId, IModel<T> rowModel) {
        item.add(new EnumLabel<E>(componentId, getDataModel(rowModel)));
    }

    @Override
    public IModel<E> getDataModel(IModel<T> rowModel) {
        return new PropertyModel<>(rowModel, getPropertyExpression());
    }
}
