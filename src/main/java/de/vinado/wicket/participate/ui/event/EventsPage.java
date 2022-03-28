package de.vinado.wicket.participate.ui.event;

import de.vinado.wicket.common.UpdateOnEventBehavior;
import de.vinado.wicket.participate.ParticipateSession;
import de.vinado.wicket.participate.components.PersonContext;
import de.vinado.wicket.participate.events.RemoveEventUpdateEvent;
import de.vinado.wicket.participate.model.EventDetails;
import de.vinado.wicket.participate.model.filters.EventFilter;
import de.vinado.wicket.participate.model.filters.ParticipantFilter;
import de.vinado.wicket.participate.services.EventService;
import de.vinado.wicket.participate.ui.pages.ParticipatePage;
import org.apache.wicket.IGenericComponent;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;

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
    private EventPanel eventPanel;

    public EventsPage(PageParameters parameters) {
        super(parameters);

        final EventDetails eventView;
        if (null == ParticipateSession.get().getEvent()) {
            if (eventService.hasUpcomingEvents()) {
                eventView = eventService.getLatestEventDetails();
            } else {
                eventView = new EventDetails();
            }
        } else {
            eventView = eventService.getEventDetails(ParticipateSession.get().getEvent());
        }

        setModel(new CompoundPropertyModel<>(eventView));
    }

    private EventDetails getLatestEventDetails() {
        EventDetails event = eventService.getLatestEventDetails();
        return null == event ? new EventDetails() : event;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        IModel<EventFilter> filterModel = new CompoundPropertyModel<>(filterModel());
        eventListPanel = new EventsPanel("events", filterModel);
        eventListPanel.add(new UpdateOnEventBehavior<>(EventTableUpdateIntent.class));
        eventListPanel.add(new UpdateOnEventBehavior<>(EventFilterIntent.class));
        eventListPanel.setOutputMarkupId(true);
        add(eventListPanel);

        IModel<ParticipantFilter> participantFilter = new CompoundPropertyModel<>(new ParticipantFilter());
        eventPanel = new EventPanel("event", null, getModel(), true, personContext, participantFilter) {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisible(eventService.hasUpcomingEvents());
            }
        };
        eventPanel.setOutputMarkupPlaceholderTag(true);
        add(eventPanel);
    }

    private EventFilter filterModel() {
        EventFilter existing = ParticipateSession.get().getEventFilter();
        return null == existing ? new EventFilter() : existing;
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
                setModel(new CompoundPropertyModel<>(getLatestEventDetails()));
            }

            target.add(eventListPanel);
            target.add(eventPanel);
        }
    }
}
