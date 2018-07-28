package de.vinado.wicket.participate.components.tables.columns;

import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.basic.EnumLabel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

public class EnumColumn<T, S, E extends Enum<E>> extends PropertyColumn<T, S> {

    public EnumColumn(final IModel<String> displayModel, final S sortProperty, final String propertyExpression) {
        super(displayModel, sortProperty, propertyExpression);
    }

    public EnumColumn(final IModel<String> displayModel, final String propertyExpression) {
        super(displayModel, propertyExpression);
    }

    @Override
    public void populateItem(final Item<ICellPopulator<T>> item, final String componentId, final IModel<T> rowModel) {
        item.add(new EnumLabel<E>(componentId, getDataModel(rowModel)));
    }

    @Override
    public IModel<E> getDataModel(final IModel<T> rowModel) {
        return new PropertyModel<>(rowModel, getPropertyExpression());
    }
}
