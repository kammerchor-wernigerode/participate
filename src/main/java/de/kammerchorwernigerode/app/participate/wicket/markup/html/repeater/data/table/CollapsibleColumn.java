package de.kammerchorwernigerode.app.participate.wicket.markup.html.repeater.data.table;

import de.kammerchorwernigerode.app.participate.wicket.markup.html.basic.CollapsibleTextPanel;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.basic.CollapsibleTextPanel.Limit;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.basic.CollapsibleTextPanel.Toggle;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.icon.Bi;
import de.kammerchorwernigerode.app.participate.wicket.markup.html.bootstrap.link.IconAjaxLink;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.event.IEventSink;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.LambdaColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.danekja.java.util.function.serializable.SerializableFunction;

public class CollapsibleColumn<T, S> extends LambdaColumn<T, S> {

    private final Limit limit;

    public CollapsibleColumn(IModel<String> displayModel, Limit limit, SerializableFunction<T, ?> function) {
        super(displayModel, function);
        this.limit = limit;
    }

    @Override
    public Component getHeader(String componentId) {
        return new Header(componentId, getDisplayModel());
    }

    @Override
    public void populateItem(Item<ICellPopulator<T>> item, String componentId, IModel<T> rowModel) {
        IModel<?> dataModel = getDataModel(rowModel);
        CollapsibleTextPanel panel = new CollapsibleTextPanel(componentId, dataModel, limit);
        item.add(panel);
    }


    private static class Header extends Panel {

        public Header(String id, IModel<?> model) {
            super(id, model);
        }

        @Override
        protected void onInitialize() {
            super.onInitialize();

            Label headingLabel = new Label("heading", getDefaultModel());
            add(headingLabel);

            ToggleLink expandLink = new ToggleLink("expand", Model.of(Toggle.State.EXPANDED));
            expandLink.setIcon(Bi.arrows_angle_expand);
            add(expandLink);

            ToggleLink collapseLink = new ToggleLink("collapse", Model.of(Toggle.State.COLLAPSED));
            collapseLink.setIcon(Bi.arrows_angle_contract);
            add(collapseLink);
        }

        protected IEventSink getEventSink() {
            return findParent(DataTable.class);
        }


        private class ToggleLink extends IconAjaxLink<Toggle.State> {

            public ToggleLink(String id, IModel<Toggle.State> model) {
                super(id, model);
            }

            @Override
            public void onClick(AjaxRequestTarget target) {
                send(Header.this.getEventSink(), Broadcast.BREADTH, Toggle.ensure(getModelObject()));
            }
        }
    }
}
