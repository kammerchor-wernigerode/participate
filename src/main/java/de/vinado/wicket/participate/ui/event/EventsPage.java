package de.vinado.wicket.participate.ui.event;

import de.vinado.app.participate.management.wicket.ManagementSession;
import de.vinado.wicket.common.OnEventBehavior;
import de.vinado.wicket.common.UpdateOnEventBehavior;
import de.vinado.wicket.participate.components.PersonContext;
import de.vinado.wicket.participate.model.Event;
import de.vinado.wicket.participate.model.EventDetails;
import de.vinado.wicket.participate.model.filters.EventFilter;
import de.vinado.wicket.participate.model.filters.ParticipantFilter;
import de.vinado.wicket.participate.services.EventService;
import de.vinado.wicket.participate.ui.pages.ParticipatePage;
import org.apache.wicket.Component;
import org.apache.wicket.IGenericComponent;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Optional;
import java.util.function.Consumer;

public class EventsPage extends ParticipatePage implements IGenericComponent<EventDetails, EventsPage> {

    @SpringBean
    private EventService eventService;

    @SpringBean
    private PersonContext personContext;

    private EventsPanel eventListPanel;
    private Component eventPanel;

    @Override
    protected void onInitialize() {
        super.onInitialize();

        EventDetails eventView = eventDetails().orElse(null);
        setModel(new CompoundPropertyModel<>(eventView));

        eventListPanel = new EventsPanel("events", eventFilterModel());
        eventListPanel.add(new UpdateOnEventBehavior<>(EventTableUpdateIntent.class));
        eventListPanel.add(new UpdateOnEventBehavior<>(EventFilterIntent.class));
        eventListPanel.setOutputMarkupId(true);
        add(eventListPanel);

        add(eventPanel = createEventPanel("event")
            .add(new ReplaceOnEventBehavior()));
    }

    private Component createEventPanel(String id) {
        return (null == getModelObject()
            ? emptyPanel(id)
            : eventPanel(id));
    }

    private EmptyPanel emptyPanel(String id) {
        return new EmptyPanel(id);
    }

    private Component eventPanel(String id) {
        return new EventPanel(id, getModel(), true, personContext, participantFilterModel());
    }

    @Override
    public void onEvent(IEvent<?> event) {
        super.onEvent(event);

        Object eventPayload = event.getPayload();
        if (eventPayload instanceof EventSelectedEvent) {
            Optional.ofNullable(((EventSelectedEvent) eventPayload).getSelection())
                .map(eventService::getEventDetails)
                .or(this::eventDetails)
                .ifPresentOrElse(details -> {
                    getSession().setMetaData(ManagementSession.event, details.getEvent());
                    getModel().setObject(details);
                }, () -> {
                    getSession().setMetaData(ManagementSession.event, null);
                    setModelObject(null);
                });

            send(eventPanel, Broadcast.EXACT, new EventPanelUpdateIntent());
            send(eventListPanel, Broadcast.EXACT, new EventTableUpdateIntent());
        }
    }

    private Optional<EventDetails> eventDetails() {
        Session session = getSession();
        Event state = session.getMetaData(ManagementSession.event);

        if (null == state) {
            EventDetails next = eventService.getLatestEventDetails();
            session.setMetaData(ManagementSession.event, null == next ? null : next.getEvent());
            return Optional.ofNullable(next);
        }

        EventDetails details = eventService.getEventDetails(state);
        if (null != details) {
            session.setMetaData(ManagementSession.event, details.getEvent());
        }

        return Optional.ofNullable(details);
    }

    private IModel<EventFilter> eventFilterModel() {
        EventFilter existing = getSession().getMetaData(ManagementSession.eventFilter);
        return new CompoundPropertyModel<>(null == existing ? new EventFilter() : existing);
    }

    private IModel<ParticipantFilter> participantFilterModel() {
        ParticipantFilter filter = new ParticipantFilter();
        return new CompoundPropertyModel<>(filter);
    }


    private final class ReplaceOnEventBehavior extends OnEventBehavior<EventPanelUpdateIntent> {

        public ReplaceOnEventBehavior() {
            super(EventPanelUpdateIntent.class);
        }

        @Override
        protected void onEvent(Component component, EventPanelUpdateIntent intent) {
            Component replacement = createEventPanel("event")
                .add(new ReplaceOnEventBehavior());
            eventPanel.replaceWith(replacement);
            eventPanel = replacement;

            registerUpdate(eventPanel);
        }

        private void registerUpdate(Component component) {
            update(target -> target.add(component));
        }

        private void update(Consumer<AjaxRequestTarget> callback) {
            RequestCycle.get().find(AjaxRequestTarget.class).ifPresent(callback);
        }
    }
}
