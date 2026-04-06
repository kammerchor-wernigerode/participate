package de.kammerchorwernigerode.app.participate.wicket.markup.html.repeater.data.table;

import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.markup.html.basic.EnumLabel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.danekja.java.util.function.serializable.SerializableFunction;

import lombok.RequiredArgsConstructor;

public class EnumLambdaColumn<T, R extends Enum<R>, S> extends AbstractColumn<T, S> {

    private final SerializableFunction<T, R> function;

    public EnumLambdaColumn(IModel<String> displayModel, S sortProperty, SerializableFunction<T, R> function) {
        super(displayModel, sortProperty);
        this.function = function;
    }

    @Override
    public void populateItem(Item<ICellPopulator<T>> item, String componentId, IModel<T> rowModel) {
        IModel<R> dataModel = new DataModel<>(rowModel, function);
        item.add(new EnumLabel<>(componentId, dataModel));
    }


    @RequiredArgsConstructor
    private static class DataModel<T, R extends Enum<R>> implements IModel<R> {

        private final IModel<T> model;
        private final SerializableFunction<T, R> function;

        @Override
        public R getObject() {
            T before = model.getObject();

            if (null == before) {
                return null;
            } else {
                return function.apply(before);
            }
        }

        @Override
        public void detach() {
            model.detach();
        }
    }
}
