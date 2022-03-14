package de.vinado.wicket.participate.ui.event;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.vinado.wicket.bt4.tooltip.TooltipBehavior;
import de.vinado.wicket.bt4.tooltip.TooltipConfig;
import de.vinado.wicket.bt4.tooltip.TooltipConfig.Boundary;
import de.vinado.wicket.participate.ParticipateSession;
import de.vinado.wicket.participate.components.panels.AjaxLinkPanel;
import de.vinado.wicket.participate.components.tables.BootstrapAjaxDataTable;
import de.vinado.wicket.participate.model.EventDetails;
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

public class EventTable extends BootstrapAjaxDataTable<EventDetails, SerializableFunction<EventDetails, ?>> {

    private static final int ROWS_PER_PAGE = 20;

    public EventTable(String id, EventDataProvider dataProvider,
                      SerializableBiConsumer<AjaxRequestTarget, IModel<EventDetails>> selectAction) {
        super(id, columns(selectAction), dataProvider, ROWS_PER_PAGE);

        dataProvider.setSort(with(EventDetails::getStartDate), SortOrder.ASCENDING);
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

    private static List<IColumn<EventDetails, SerializableFunction<EventDetails, ?>>> columns(
        SerializableBiConsumer<AjaxRequestTarget, IModel<EventDetails>> selectAction) {
        TooltipConfig tooltipConfig = new TooltipConfig()
            .withBoundary(Boundary.window);

        return Arrays.asList(
            nameColumn(selectAction, tooltipConfig),
            dateColumn(),
            typeColumn(tooltipConfig),
            locationColumn(),
            adpColumn(tooltipConfig)
        );
    }

    private static IColumn<EventDetails, SerializableFunction<EventDetails, ?>> nameColumn(
        SerializableBiConsumer<AjaxRequestTarget, IModel<EventDetails>> selectAction,
        TooltipConfig tooltipConfig) {
        return new PropertyColumn<>(new ResourceModel("name", "Name"), with(EventDetails::getName), "name") {
            @Override
            public void populateItem(Item<ICellPopulator<EventDetails>> item, String componentId, IModel<EventDetails> rowModel) {
                AjaxLinkPanel component = new AjaxLinkPanel(componentId, new PropertyModel<>(rowModel, getPropertyExpression())) {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        selectAction.accept(target, rowModel);
                    }
                };
                component.getAjaxLink().add(new TooltipBehavior(rowModel.map(EventDetails::getName), tooltipConfig));
                item.add(component.setOutputMarkupId(true));
            }

            @Override
            public String getCssClass() {
                return "name";
            }
        };
    }

    private static IColumn<EventDetails, SerializableFunction<EventDetails, ?>> dateColumn() {
        return new PropertyColumn<>(new ResourceModel("date", "Date"), with(EventDetails::getStartDate), "displayDate") {
            @Override
            public String getCssClass() {
                return "date";
            }
        };
    }

    private static IColumn<EventDetails, SerializableFunction<EventDetails, ?>> typeColumn(TooltipConfig tooltipConfig) {
        return new PropertyColumn<>(new ResourceModel("event", "Event"), with(EventDetails::getEventType), "eventType") {
            @Override
            public void populateItem(Item<ICellPopulator<EventDetails>> item, String componentId, IModel<EventDetails> rowModel) {
                item.add(new Label(componentId, getDataModel(rowModel))
                    .add(new TooltipBehavior(rowModel.map(EventDetails::getEventType), tooltipConfig))
                    .setOutputMarkupId(true));
            }

            @Override
            public String getCssClass() {
                return "type";
            }
        };
    }

    private static IColumn<EventDetails, SerializableFunction<EventDetails, ?>> locationColumn() {
        return new PropertyColumn<>(new ResourceModel("location", "Location"), with(EventDetails::getLocation), "location") {
            @Override
            public String getCssClass() {
                return "location";
            }
        };
    }

    private static IColumn<EventDetails, SerializableFunction<EventDetails, ?>> adpColumn(TooltipConfig tooltipConfig) {
        return new PropertyColumn<>(new ResourceModel("event.a-d-p.short", "A/D/P"), "countAcceptedDeclinedPending") {
            @Override
            public void populateItem(Item<ICellPopulator<EventDetails>> item, String componentId, IModel<EventDetails> rowModel) {
                item.add(new Label(componentId, getDataModel(rowModel))
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
    protected Item<EventDetails> newRowItem(String id, int index, IModel<EventDetails> model) {
        final Item<EventDetails> item = super.newRowItem(id, index, model);
        final Long sessionEventId = ParticipateSession.get().getEvent().getId();
        if (null != sessionEventId && model.getObject().getId().equals(sessionEventId))
            item.add(new CssClassNameAppender("table-primary"));
        return item;
    }

    protected static <T, R> SerializableFunction<T, R> with(SerializableFunction<T, R> function) {
        return function;
    }
}
