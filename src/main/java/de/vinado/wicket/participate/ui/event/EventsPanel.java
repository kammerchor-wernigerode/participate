package de.vinado.wicket.participate.ui.event;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome6IconType;
import de.vinado.app.participate.event.app.InvitationCommandHandler;
import de.vinado.app.participate.event.app.SendBulkInvitations;
import de.vinado.app.participate.management.wicket.ManagementSession;
import de.vinado.app.participate.notification.email.model.EmailException;
import de.vinado.app.participate.wicket.bt5.modal.Modal;
import de.vinado.app.participate.wicket.spring.Holder;
import de.vinado.wicket.common.UpdateOnEventBehavior;
import de.vinado.wicket.participate.components.panels.BootstrapPanel;
import de.vinado.wicket.participate.components.snackbar.Snackbar;
import de.vinado.wicket.participate.model.Event;
import de.vinado.wicket.participate.model.EventDetails;
import de.vinado.wicket.participate.model.dtos.EventDTO;
import de.vinado.wicket.participate.model.filters.EventFilter;
import de.vinado.wicket.participate.services.EventService;
import lombok.extern.slf4j.Slf4j;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.danekja.java.util.function.serializable.SerializableConsumer;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class EventsPanel extends BootstrapPanel<EventFilter> {

    @SpringBean
    @SuppressWarnings("unused")
    private EventService eventService;

    private final Modal modal;
    private final IModel<List<SelectableEventDetails>> model;
    private final EventDataProvider dataProvider;

    public EventsPanel(String id, IModel<EventFilter> filterModel) {
        super(id, filterModel);

        this.modal = modal("modal");
        this.model = eventListModel();
        this.dataProvider = dataProvider();
    }

    protected Modal modal(String wicketId) {
        return new Modal(wicketId);
    }

    private IModel<List<SelectableEventDetails>> eventListModel() {
        return new LoadableDetachableModel<>() {

            @Override
            protected List<SelectableEventDetails> load() {
                return (getModel().getObject().isShowAll()
                    ? eventService.listAll()
                    : eventService.getUpcomingEventDetails().stream())
                    .map(SelectableEventDetails::new)
                    .collect(Collectors.toList());
            }
        };
    }

    private EventDataProvider dataProvider() {
        return new EventDataProvider(model, getModel());
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        add(modal);

        addQuickAccessAction(AjaxAction.create(new ResourceModel("event.add", "Add Event"),
            FontAwesome6IconType.plus_s,
            this::onAdd));
        addQuickAccessAction(wicketId -> new AbstractAction(wicketId,
            new ResourceModel("email.send.invitation", "Send Invitation"),
            FontAwesome6IconType.square_envelope_s) {

            @Override
            protected AbstractLink link(String wicketId) {
                return new SendInvitationLink(wicketId, dataProvider);
            }
        });

        add(filter());
        add(eventTable());
    }

    private Component filter() {
        return new EventFilterForm("filter", getModel()) {
            @Override
            protected void onApply() {
                getSession().setMetaData(ManagementSession.eventFilter, getModelObject());
                model.detach();
                send(EventsPanel.this, Broadcast.BREADTH, new EventFilterIntent(getModelObject()));
            }
        };
    }

    private EventTable eventTable() {
        EventTable dataTable = new EventTable("dataTable", dataProvider, this::selectEvent);
        dataTable.setDefaultModel(getModel());
        return dataTable;
    }

    private void selectEvent(AjaxRequestTarget target, IModel<SelectableEventDetails> model) {
        Event event = model.getObject().getEvent();
        send(getWebPage(), Broadcast.BREADTH, new EventSelectedEvent(event));
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
            EventsPanel.this.model.detach();
            send(getWebPage(), Broadcast.BREADTH, new EventSelectedEvent(event));
            Snackbar.show(target, new ResourceModel("event.add.success", "A new event has been added"));
        };
    }


    private static class SendInvitationLink extends AjaxLink<Void> {

        @SpringBean
        private Holder<InvitationCommandHandler> commandHandler;

        private final EventDataProvider dataProvider;

        public SendInvitationLink(String id, EventDataProvider dataProvider) {
            super(id);

            this.dataProvider = dataProvider;
        }

        @Override
        protected void onInitialize() {
            super.onInitialize();

            add(new UpdateOnEventBehavior<>(ToggleEventSelectionIntent.class));
        }

        @Override
        protected void onConfigure() {
            super.onConfigure();

            setEnabled(dataProvider.hasSelected());
        }

        @Override
        public void onClick(AjaxRequestTarget target) {
            List<Event> events = dataProvider.listSelected()
                .map(EventDetails::getEvent)
                .filter(Event::isUpcoming)
                .toList();

            SendBulkInvitations command = new SendBulkInvitations(events);
            execute(command, target);
        }

        private void execute(SendBulkInvitations command, AjaxRequestTarget target) {
            try {
                commandHandler().execute(command);
                ResourceModel message = new ResourceModel("event.invitations.sent.success", "Invitations have been sent");
                Snackbar.show(target, message);
                success(message.getObject());
            } catch (EmailException e) {
                log.error("Failed to send invitations", e);
                IModel<String> message = new ResourceModel("event.invitations.sent.error", "An error occurred while sending the invitations");
                Snackbar.show(target, message);
                error(message);
            }
        }

        private InvitationCommandHandler commandHandler() {
            return commandHandler.service();
        }
    }
}
