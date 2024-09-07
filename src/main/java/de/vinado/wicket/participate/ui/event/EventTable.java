package de.vinado.wicket.participate.ui.event;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.jquery.util.Json;
import de.vinado.app.participate.management.wicket.ManagementSession;
import de.vinado.app.participate.wicket.bt5.tooltip.TooltipBehavior;
import de.vinado.app.participate.wicket.bt5.tooltip.TooltipConfig;
import de.vinado.wicket.participate.components.panels.AjaxLinkPanel;
import de.vinado.wicket.participate.components.tables.BootstrapAjaxDataTable;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.DefaultItemReuseStrategy;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.danekja.java.util.function.serializable.SerializableBiConsumer;
import org.danekja.java.util.function.serializable.SerializableFunction;

import java.util.Arrays;
import java.util.List;

public class EventTable extends BootstrapAjaxDataTable<SelectableEventDetails, SerializableFunction<SelectableEventDetails, ?>> {

    private static final int ROWS_PER_PAGE = 20;

    public EventTable(String id, EventDataProvider dataProvider,
                      SerializableBiConsumer<AjaxRequestTarget, IModel<SelectableEventDetails>> selectAction) {
        super(id, columns(selectAction), dataProvider, ROWS_PER_PAGE);

        dataProvider.setSort(with(SelectableEventDetails::getStartDate), SortOrder.ASCENDING);
        setOutputMarkupId(true);
        condensed().hover();

        // Force redraw
        setItemReuseStrategy(DefaultItemReuseStrategy.getInstance());
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(new CssClassNameAppender("events"));
    }

    private static List<IColumn<SelectableEventDetails, SerializableFunction<SelectableEventDetails, ?>>> columns(
        SerializableBiConsumer<AjaxRequestTarget, IModel<SelectableEventDetails>> selectAction) {
        TooltipConfig tooltipConfig = new TooltipConfig()
            .withBoundary(new Json.RawValue("document.body"));

        return Arrays.asList(
            nameColumn(selectAction, tooltipConfig),
            dateColumn(),
            typeColumn(tooltipConfig),
            locationColumn(),
            adpColumn(tooltipConfig)
        );
    }

    private static IColumn<SelectableEventDetails, SerializableFunction<SelectableEventDetails, ?>> nameColumn(
        SerializableBiConsumer<AjaxRequestTarget, IModel<SelectableEventDetails>> selectAction,
        TooltipConfig tooltipConfig) {
        return new PropertyColumn<>(new ResourceModel("name", "Name"), with(SelectableEventDetails::getName), "name") {
            @Override
            public void populateItem(Item<ICellPopulator<SelectableEventDetails>> item, String componentId, IModel<SelectableEventDetails> rowModel) {
                AjaxLinkPanel component = new AjaxLinkPanel(componentId, new PropertyModel<>(rowModel, getPropertyExpression())) {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        selectAction.accept(target, rowModel);
                    }
                };
                component.getAjaxLink().add(new TooltipBehavior(rowModel.map(SelectableEventDetails::getName), tooltipConfig));
                item.add(component.setOutputMarkupId(true));
            }

            @Override
            public String getCssClass() {
                return "name";
            }
        };
    }

    private static IColumn<SelectableEventDetails, SerializableFunction<SelectableEventDetails, ?>> dateColumn() {
        return new PropertyColumn<>(new ResourceModel("date", "Date"), with(SelectableEventDetails::getStartDate), "displayDate") {
            @Override
            public String getCssClass() {
                return "date";
            }
        };
    }

    private static IColumn<SelectableEventDetails, SerializableFunction<SelectableEventDetails, ?>> typeColumn(TooltipConfig tooltipConfig) {
        return new PropertyColumn<>(new ResourceModel("event", "Event"), with(SelectableEventDetails::getEventType), "eventType") {
            @Override
            public void populateItem(Item<ICellPopulator<SelectableEventDetails>> item, String componentId, IModel<SelectableEventDetails> rowModel) {
                item.add(new Label(componentId, getDataModel(rowModel))
                    .add(new TooltipBehavior(rowModel.map(SelectableEventDetails::getEventType), tooltipConfig))
                    .setOutputMarkupId(true));
            }

            @Override
            public String getCssClass() {
                return "type";
            }
        };
    }

    private static IColumn<SelectableEventDetails, SerializableFunction<SelectableEventDetails, ?>> locationColumn() {
        return new PropertyColumn<>(new ResourceModel("location", "Location"), with(SelectableEventDetails::getLocation), "location") {
            @Override
            public String getCssClass() {
                return "location";
            }
        };
    }

    private static IColumn<SelectableEventDetails, SerializableFunction<SelectableEventDetails, ?>> adpColumn(TooltipConfig tooltipConfig) {
        return new PropertyColumn<>(new ResourceModel("event.a-d-p.short", "A/D/P"), "countAcceptedDeclinedPending") {
            @Override
            public void populateItem(Item<ICellPopulator<SelectableEventDetails>> item, String componentId, IModel<SelectableEventDetails> rowModel) {
                item.add(new ParticipationMeter(componentId, rowModel.map(SelectableEventDetails::getSubject))
                    .add(new TooltipBehavior(new ResourceModel("event.a-d-p", "Accepted/Declined/Pending"), tooltipConfig))
                    .setOutputMarkupId(true));
            }

            @Override
            public String getCssClass() {
                return "adp";
            }
        };
    }

    @Override
    protected Item<SelectableEventDetails> newRowItem(String id, int index, IModel<SelectableEventDetails> model) {
        Item<SelectableEventDetails> item = super.newRowItem(id, index, model);
        Long sessionEventId = Session.get().getMetaData(ManagementSession.event).getId();
        if (null != sessionEventId && model.getObject().getId().equals(sessionEventId))
            item.add(new CssClassNameAppender("table-primary"));
        return item;
    }

    protected static <T, R> SerializableFunction<T, R> with(SerializableFunction<T, R> function) {
        return function;
    }
}
