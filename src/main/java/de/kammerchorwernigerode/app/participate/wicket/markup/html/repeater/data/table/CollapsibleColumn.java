package de.kammerchorwernigerode.app.participate.wicket.markup.html.repeater.data.table;

import de.kammerchorwernigerode.app.participate.wicket.markup.html.basic.CollapsibleTextPanel;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.basic.CollapsibleTextPanel.Limit;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.LambdaColumn;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.danekja.java.util.function.serializable.SerializableFunction;

public class CollapsibleColumn<T, S> extends LambdaColumn<T, S> {

    private final Limit limit;

    public CollapsibleColumn(IModel<String> displayModel, Limit limit, SerializableFunction<T, ?> function) {
        super(displayModel, function);
        this.limit = limit;
    }

    @Override
    public void populateItem(Item<ICellPopulator<T>> item, String componentId, IModel<T> rowModel) {
        IModel<?> dataModel = getDataModel(rowModel);
        CollapsibleTextPanel panel = new CollapsibleTextPanel(componentId, dataModel, limit);
        item.add(panel);
    }
}
