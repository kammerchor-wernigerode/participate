package de.vinado.wicket.participate.ui.event;

import de.agilecoders.wicket.core.markup.html.bootstrap.navigation.Breadcrumb;
import de.vinado.wicket.participate.ParticipateSession;
import de.vinado.wicket.participate.components.PersonContext;
import de.vinado.wicket.participate.events.RemoveEventUpdateEvent;
import de.vinado.wicket.participate.model.EventDetails;
import de.vinado.wicket.participate.model.filters.ParticipantFilter;
import de.vinado.wicket.participate.services.EventService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class EventMasterPanel extends BreadCrumbPanel {

    @SpringBean
    @SuppressWarnings("unused")
    private EventService eventService;

    @SpringBean
    private PersonContext personContext;

    private EventsPanel eventListPanel;
    private EventPanel eventPanel;

    public EventMasterPanel(final String id, final IBreadCrumbModel breadCrumbModel) {
        super(id, breadCrumbModel);

        ((Breadcrumb) getBreadCrumbModel()).setVisible(false);

        eventListPanel = new EventsPanel("events", LoadableDetachableModel.of(eventService::getUpcomingEventDetails)) {
            @Override
            protected void onAfterAdd(AjaxRequestTarget target) {
                super.onAfterAdd(target);
                target.add(eventPanel);
            }
        };
        eventListPanel.setOutputMarkupId(true);
        add(eventListPanel);

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

        IModel<ParticipantFilter> participantFilter = new CompoundPropertyModel<>(new ParticipantFilter());
        CompoundPropertyModel<EventDetails> eventModel = new CompoundPropertyModel<>(eventView);
        eventPanel = new EventPanel("event", breadCrumbModel, eventModel, true, personContext, participantFilter) {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisible(eventService.hasUpcomingEvents());
            }
        };
        eventPanel.setOutputMarkupPlaceholderTag(true);
        add(eventPanel);
    }

    @Override
    public void onEvent(final IEvent<?> iEvent) {
        super.onEvent(iEvent);
        final Object payload = iEvent.getPayload();

        if (payload instanceof RemoveEventUpdateEvent) {
            final RemoveEventUpdateEvent event = (RemoveEventUpdateEvent) payload;
            final AjaxRequestTarget target = event.getTarget();

            if (!eventService.hasUpcomingEvents()) {
                ParticipateSession.get().setEvent(null);
            } else {
                ParticipateSession.get().setEvent(eventService.getLatestEvent());
                setDefaultModel(new CompoundPropertyModel<>(eventService.getLatestEventDetails()));
            }

            target.add(eventListPanel);
            target.add(eventPanel);
        }
    }

    @Override
    public IModel<String> getTitle() {
        return new ResourceModel("events", "Events");
    }
}
