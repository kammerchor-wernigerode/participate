package de.kammerchorwernigerode.app.participate.wicket.markup.html.repeater.data.table;

import de.kammerchorwernigerode.app.participate.wicket.markup.html.Anchor;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.LambdaColumn;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.danekja.java.util.function.serializable.SerializableFunction;

public abstract class LinkColumn<T, S> extends LambdaColumn<T, S> {

    public LinkColumn(IModel<String> displayModel, SerializableFunction<T, ?> function) {
        super(displayModel, function);
    }

    public LinkColumn(IModel<String> displayModel, S sortProperty, SerializableFunction<T, ?> function) {
        super(displayModel, sortProperty, function);
    }

    @Override
    public void populateItem(Item<ICellPopulator<T>> item, String componentId, IModel<T> rowModel) {
        Anchor anchor = new Anchor(componentId);
        anchor.setRenderBodyOnly(true);

        CellLink link = new CellLink(anchor.getLinkId(), rowModel);
        link.setBody(getDataModel(rowModel));

        anchor.add(link);
        item.add(anchor);
    }

    protected abstract void onClick(AjaxRequestTarget target, IModel<T> rowModel);


    private class CellLink extends AjaxLink<T> {

        public CellLink(String id, IModel<T> model) {
            super(id, model);
        }

        @Override
        public void onClick(AjaxRequestTarget target) {
            IModel<T> model = getModel();
            LinkColumn.this.onClick(target, model);
        }
    }
}
