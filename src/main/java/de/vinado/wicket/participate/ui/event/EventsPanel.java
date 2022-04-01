package de.vinado.wicket.participate.ui.event;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome5IconType;
import de.vinado.wicket.bt4.modal.ModalAnchor;
import de.vinado.wicket.participate.ParticipateSession;
import de.vinado.wicket.participate.components.panels.BootstrapPanel;
import de.vinado.wicket.participate.components.snackbar.Snackbar;
import de.vinado.wicket.participate.model.Event;
import de.vinado.wicket.participate.model.EventDetails;
import de.vinado.wicket.participate.model.dtos.EventDTO;
import de.vinado.wicket.participate.model.filters.EventFilter;
import de.vinado.wicket.participate.services.EventService;
import de.vinado.wicket.participate.ui.pages.BasePage;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class EventsPanel extends BootstrapPanel<EventFilter> {

    @SpringBean
    @SuppressWarnings("unused")
    private EventService eventService;

    public EventsPanel(String id, IModel<EventFilter> filterModel) {
        super(id, filterModel);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        addQuickAccessAction(AjaxAction.create(new ResourceModel("event.add", "Add Event"),
            FontAwesome5IconType.plus_s,
            this::onAdd));

        add(filter());
        add(eventTable());
    }

    private Component filter() {
        return new EventFilterForm("filter", getModel()) {
            @Override
            protected void onApply() {
                ParticipateSession.get().setEventFilter(getModelObject());
                send(EventsPanel.this, Broadcast.BREADTH, new EventFilterIntent(getModelObject()));
            }
        };
    }

    private EventTable eventTable() {
        EventTable dataTable = new EventTable("dataTable", dataProvider(), this::selectEvent);
        dataTable.setDefaultModel(getModel());
        return dataTable;
    }

    private void selectEvent(AjaxRequestTarget target, IModel<EventDetails> model) {
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
        ModalAnchor modal = ((BasePage) getWebPage()).getModalAnchor();
        modal.setContent(new AddEditEventPanel(modal, new ResourceModel("event.add", "Add Event"), new CompoundPropertyModel<>(new EventDTO())) {
            @Override
            public void onUpdate(final Event savedEvent, final AjaxRequestTarget target) {
                send(getWebPage(), Broadcast.BREADTH, new EventSelectedEvent(savedEvent));
                Snackbar.show(target, new ResourceModel("event.add.success", "A new event has been added"));
            }
        });
        modal.show(target);
    }
}
