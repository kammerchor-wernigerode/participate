package de.vinado.wicket.participate.ui.event;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.components.TooltipBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.components.TooltipConfig;
import de.vinado.wicket.participate.ParticipateSession;
import de.vinado.wicket.participate.components.panels.AjaxLinkPanel;
import de.vinado.wicket.participate.components.tables.BootstrapAjaxDataTable;
import de.vinado.wicket.participate.events.AjaxUpdateEvent;
import de.vinado.wicket.participate.events.EventUpdateEvent;
import de.vinado.wicket.participate.model.EventDetails;
import de.vinado.wicket.participate.model.filters.EventFilter;
import de.vinado.wicket.participate.providers.SimpleDataProvider;
import de.vinado.wicket.participate.services.EventService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.time.Duration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class EventsPanel extends Panel {

    @SpringBean
    @SuppressWarnings("unused")
    private EventService eventService;

    private IModel<List<EventDetails>> model;

    private SimpleDataProvider<EventDetails, String> dataProvider;
    private BootstrapAjaxDataTable<EventDetails, String> dataTable;

    public EventsPanel(final String id, final IModel<List<EventDetails>> model) {
        super(id, model);
        this.model = model;

        final TooltipConfig tooltipConfig = new TooltipConfig();
        tooltipConfig.withDelay(Duration.milliseconds(300L));
        tooltipConfig.withHtml(true);

        final EventFilter eventFilter = ParticipateSession.get().getEventFilter();

        final EventFilterPanel filterPanel = new EventFilterPanel("filterPanel", model,
            new CompoundPropertyModel<>(null != eventFilter ? eventFilter : new EventFilter())) {
            @Override
            public SimpleDataProvider<EventDetails, ?> getDataProvider() {
                return dataProvider;
            }

            @Override
            public DataTable<EventDetails, ?> getDataTable() {
                return dataTable;
            }
        };
        add(filterPanel);

        dataProvider = new SimpleDataProvider<EventDetails, String>(model.getObject()) {
            @Override
            public String getDefaultSort() {
                return "startDate";
            }
        };

        final List<IColumn<EventDetails, String>> columns = new ArrayList<>();
        columns.add(new PropertyColumn<EventDetails, String>(new ResourceModel("name", "Name"), "name", "name") {
            @Override
            public void populateItem(Item<ICellPopulator<EventDetails>> item, String componentId, IModel<EventDetails> rowModel) {
                final AjaxLinkPanel selectEventLink = new AjaxLinkPanel(componentId, new PropertyModel<>(rowModel, getPropertyExpression())) {
                    @Override
                    public void onClick(final AjaxRequestTarget target) {
                        ParticipateSession.get().setEvent(rowModel.getObject().getEvent());
                        send(getPage(), Broadcast.BREADTH, new EventUpdateEvent(rowModel.getObject().getEvent(), target));
                        target.add(dataTable);
                    }
                };
                selectEventLink.getAjaxLink().add(new TooltipBehavior(Model.of(rowModel.getObject().getName()), tooltipConfig));
                item.add(selectEventLink);
            }

            @Override
            public String getCssClass() {
                return "td-max-width-300";
            }
        });
        columns.add(new PropertyColumn<EventDetails, String>(new ResourceModel("date", "Date"), "startDate", "displayDate") {
            @Override
            public String getCssClass() {
                return "td-max-width-185";
            }
        });
        columns.add(new PropertyColumn<EventDetails, String>(new ResourceModel("event", "Event"), "eventType", "eventType") {
            @Override
            public void populateItem(Item<ICellPopulator<EventDetails>> item, String componentId, IModel<EventDetails> rowModel) {
                final Label eventTypeLabel = new Label(componentId, getDataModel(rowModel));
                eventTypeLabel.add(new TooltipBehavior(Model.of(rowModel.getObject().getEventType()), tooltipConfig));
                item.add(eventTypeLabel);
            }

            @Override
            public String getCssClass() {
                return "td-max-width-200";
            }
        });
        columns.add(new PropertyColumn<>(new ResourceModel("location", "Location"), "location", "location"));
        columns.add(new PropertyColumn<EventDetails, String>(new ResourceModel("event.a-d-p.short", "A/D/P"), "countAcceptedDeclinedPending") {
            @Override
            public void populateItem(final Item<ICellPopulator<EventDetails>> item, final String componentId, final IModel<EventDetails> rowModel) {
                final Label eventTypeLabel = new Label(componentId, getDataModel(rowModel));
                eventTypeLabel.add(new TooltipBehavior(new ResourceModel("event.a-d-p", "Accepted/Declined/Pending"), tooltipConfig));
                item.add(eventTypeLabel);
            }
        });

        dataTable = new BootstrapAjaxDataTable<EventDetails, String>("dataTable", columns, dataProvider, 10) {
            @Override
            protected Item<EventDetails> newRowItem(final String id, final int index, final IModel<EventDetails> model) {
                final Item<EventDetails> item = super.newRowItem(id, index, model);
                final Long sessionEventId = ParticipateSession.get().getEvent().getId();
                if (null != sessionEventId && model.getObject().getId().equals(sessionEventId))
                    item.add(new CssClassNameAppender("info"));
                return item;
            }
        };
        dataTable.setOutputMarkupId(true);
        dataTable.hover().condensed();
        add(dataTable);
    }

    @Override
    public void onEvent(final IEvent<?> iEvent) {
        super.onEvent(iEvent);
        final Object payload = iEvent.getPayload();
        if (payload instanceof AjaxUpdateEvent) {
            final AjaxUpdateEvent updateEvent = (AjaxUpdateEvent) payload;
            final AjaxRequestTarget target = updateEvent.getTarget();
            model.setObject(eventService.getUpcomingEventDetails());
            dataProvider.set(model.getObject());
            target.add(dataTable);
        }
    }
}
