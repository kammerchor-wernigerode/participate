package de.vinado.wicket.participate.ui.event;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome6IconType;
import de.vinado.app.participate.management.wicket.ManagementSession;
import de.vinado.app.participate.wicket.bt5.modal.Modal;
import de.vinado.wicket.participate.components.panels.BootstrapPanel;
import de.vinado.wicket.participate.components.snackbar.Snackbar;
import de.vinado.wicket.participate.model.Event;
import de.vinado.wicket.participate.model.dtos.EventDTO;
import de.vinado.wicket.participate.model.filters.EventFilter;
import de.vinado.wicket.participate.services.EventService;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.danekja.java.util.function.serializable.SerializableConsumer;

public class EventsPanel extends BootstrapPanel<EventFilter> {

    @SpringBean
    @SuppressWarnings("unused")
    private EventService eventService;

    private final Modal modal;

    public EventsPanel(String id, IModel<EventFilter> filterModel) {
        super(id, filterModel);

        this.modal = modal("modal");
    }

    protected Modal modal(String wicketId) {
        return new Modal(wicketId);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(modal);

        addQuickAccessAction(AjaxAction.create(new ResourceModel("event.add", "Add Event"),
            FontAwesome6IconType.plus_s,
            this::onAdd));

        add(filter());
        add(eventTable());
    }

    private Component filter() {
        return new EventFilterForm("filter", getModel()) {
            @Override
            protected void onApply() {
                getSession().setMetaData(ManagementSession.eventFilter, getModelObject());
                send(EventsPanel.this, Broadcast.BREADTH, new EventFilterIntent(getModelObject()));
            }
        };
    }

    private EventTable eventTable() {
        EventTable dataTable = new EventTable("dataTable", dataProvider(), this::selectEvent);
        dataTable.setDefaultModel(getModel());
        return dataTable;
    }

    private void selectEvent(AjaxRequestTarget target, IModel<SelectableEventDetails> model) {
        Event event = model.getObject().getEvent();
        send(getWebPage(), Broadcast.BREADTH, new EventSelectedEvent(event));
    }

    private EventDataProvider dataProvider() {
        return new EventDataProvider(getModel(), eventService);
    }

    @Override
    protected IModel<String> titleModel() {
        return new ResourceModel("overview", "Overview");
    }

    private void onAdd(AjaxRequestTarget target) {
        CompoundPropertyModel<EventDTO> model = new CompoundPropertyModel<>(new EventDTO());
        ResourceModel title = new ResourceModel("event.add", "Add Event");

        modal
            .size(Modal.Size.LARGE)
            .title(title)
            .content(new AddEditEventPanel(modal.getContentId(), model))
            .addCloseAction(new ResourceModel("cancel", "Cancel"))
            .addSubmitAction(new ResourceModel("save", "Save"), update(model))
            .show(target);
    }

    private SerializableConsumer<AjaxRequestTarget> update(IModel<EventDTO> model) {
        return target -> {
            Event event = model.map(EventDTO::getEvent).getObject();
            send(getWebPage(), Broadcast.BREADTH, new EventSelectedEvent(event));
            Snackbar.show(target, new ResourceModel("event.add.success", "A new event has been added"));
        };
    }
}
