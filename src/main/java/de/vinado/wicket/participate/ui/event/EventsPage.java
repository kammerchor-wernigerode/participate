package de.vinado.wicket.participate.ui.event;

import de.vinado.wicket.common.UpdateOnEventBehavior;
import de.vinado.wicket.participate.ParticipateSession;
import de.vinado.wicket.participate.components.PersonContext;
import de.vinado.wicket.participate.events.RemoveEventUpdateEvent;
import de.vinado.wicket.participate.model.Event;
import de.vinado.wicket.participate.model.EventDetails;
import de.vinado.wicket.participate.model.filters.EventFilter;
import de.vinado.wicket.participate.model.filters.ParticipantFilter;
import de.vinado.wicket.participate.services.EventService;
import de.vinado.wicket.participate.ui.pages.ParticipatePage;
import org.apache.wicket.Component;
import org.apache.wicket.IGenericComponent;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Optional;

/**
 * @author Vincent Nadoll
 */
public class EventsPage extends ParticipatePage implements IGenericComponent<EventDetails, EventsPage> {

    private static final long serialVersionUID = -4123194846716920977L;

    @SpringBean
    private EventService eventService;

    @SpringBean
    private PersonContext personContext;

    private EventsPanel eventListPanel;
    private Component eventPanel;

    public EventsPage(PageParameters parameters) {
        super(parameters);

        EventDetails eventView = eventDetails().orElse(null);
        setModel(new CompoundPropertyModel<>(eventView));
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        eventListPanel = new EventsPanel("events", eventFilterModel());
        eventListPanel.add(new UpdateOnEventBehavior<>(EventTableUpdateIntent.class));
        eventListPanel.add(new UpdateOnEventBehavior<>(EventFilterIntent.class));
        eventListPanel.setOutputMarkupId(true);
        add(eventListPanel);

        add(eventPanel = createEventPanel("event")
            .add(new UpdateOnEventBehavior<>(EventSelectedEvent.class, this::onSelect)));
    }

    private void onSelect(EventSelectedEvent intent) {
        Event selection = intent.getSelection();
        EventDetails details = eventService.getEventDetails(selection);
        setModelObject(details);

        Component replacement = createEventPanel("event")
            .add(new UpdateOnEventBehavior<>(EventSelectedEvent.class, this::onSelect));
        eventPanel.replaceWith(replacement);
        eventPanel = replacement;

        RequestCycle.get().find(AjaxRequestTarget.class)
            .ifPresent(target -> target.add(eventPanel));
    }

    private Component createEventPanel(String id) {
        return (null == getModelObject()
            ? emptyPanel(id)
            : eventPanel(id))
            .setOutputMarkupId(true);
    }

    private EmptyPanel emptyPanel(String id) {
        return new EmptyPanel(id);
    }

    private Component eventPanel(String id) {
        return new EventPanel(id, getModel(), true, personContext, participantFilterModel());
    }

    @Override
    public void onEvent(IEvent<?> iEvent) {
        super.onEvent(iEvent);
        Object payload = iEvent.getPayload();

        if (payload instanceof RemoveEventUpdateEvent) {
            RemoveEventUpdateEvent event = (RemoveEventUpdateEvent) payload;
            AjaxRequestTarget target = event.getTarget();

            if (!eventService.hasUpcomingEvents()) {
                ParticipateSession.get().setEvent(null);
            } else {
                ParticipateSession.get().setEvent(eventService.getLatestEvent());
                setModelObject(eventDetails().orElse(null));
            }

            target.add(eventListPanel);
            target.add(eventPanel);
        }
    }

    private Optional<EventDetails> eventDetails() {
        ParticipateSession session = ParticipateSession.get();
        Event state = session.getEvent();

        if (null == state) {
            EventDetails next = eventService.getLatestEventDetails();
            session.setEvent(null == next ? null : next.getEvent());
            return Optional.ofNullable(next);
        }

        EventDetails details = eventService.getEventDetails(state);
        if (null != details) {
            session.setEvent(details.getEvent());
        }

        return Optional.ofNullable(details);
    }

    private IModel<EventFilter> eventFilterModel() {
        EventFilter existing = ParticipateSession.get().getEventFilter();
        return new CompoundPropertyModel<>(null == existing ? new EventFilter() : existing);
    }

    private IModel<ParticipantFilter> participantFilterModel() {
        ParticipantFilter filter = new ParticipantFilter();
        return new CompoundPropertyModel<>(filter);
    }
}
