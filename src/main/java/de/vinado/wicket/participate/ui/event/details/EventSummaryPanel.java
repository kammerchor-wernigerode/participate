package de.vinado.wicket.participate.ui.event.details;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome5IconType;
import de.vinado.wicket.participate.ParticipateSession;
import de.vinado.wicket.participate.behavoirs.UpdateOnEventBehavior;
import de.vinado.wicket.participate.events.AjaxUpdateEvent;
import de.vinado.wicket.participate.events.EventSummaryUpdateEvent;
import de.vinado.wicket.participate.model.EventDetails;
import de.vinado.wicket.participate.model.filters.ParticipantFilter;
import de.vinado.wicket.participate.services.EventService;
import de.vinado.wicket.participate.ui.event.EventsPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class EventSummaryPanel extends BreadCrumbPanel {

    @SpringBean
    @SuppressWarnings("unused")
    private EventService eventService;

    private IModel<EventDetails> model;

    private Form form;

    public EventSummaryPanel(final String id, final IBreadCrumbModel breadCrumbModel, final IModel<EventDetails> model,
                             final boolean editable) {
        super(id, breadCrumbModel, model);

        this.model = model;

        form = new Form("form");
        add(form);

        final WebMarkupContainer wmc = new WebMarkupContainer("wmc");
        wmc.setOutputMarkupId(true);
        form.add(wmc);

        wmc.add(new Label("eventName", new PropertyModel<>(model, "name")));

        // TODO Stylen
        final BootstrapAjaxLink<Void> previousEventBtn = new BootstrapAjaxLink<>("previousEventBtn", Buttons.Type.Link) {
            @Override
            public void onClick(final AjaxRequestTarget target) {
                final EventDetails previousEvent =
                    eventService.getPredecessor(model.getObject());
                if (null != previousEvent) {
                    ParticipateSession.get().setEvent(previousEvent.getEvent());
                    send(getWebPage(), Broadcast.BREADTH, new AjaxUpdateEvent(target));
                    model.setObject(previousEvent);
                    target.add(form);
                }
            }
        };
        previousEventBtn.setOutputMarkupPlaceholderTag(true);
        previousEventBtn.setSize(Buttons.Size.Small);
        previousEventBtn.setIconType(FontAwesome5IconType.caret_left_s);
        wmc.add(previousEventBtn);

        final BootstrapAjaxLink<Void> backBtn = new BootstrapAjaxLink<>("backBtn", Buttons.Type.Default) {
            @Override
            public void onClick(final AjaxRequestTarget target) {
                setResponsePage(EventsPage.class);
            }
        };
        backBtn.setLabel(new ResourceModel("show.event.overview", "Show Event Overview"));
        backBtn.setSize(Buttons.Size.Small);
        backBtn.setIconType(FontAwesome5IconType.calendar_s);
        wmc.add(backBtn);

        final BootstrapAjaxLink<Void> nextEventBtn = new BootstrapAjaxLink<>("nextEventBtn", Buttons.Type.Link) {
            @Override
            public void onClick(final AjaxRequestTarget target) {
                final EventDetails nextEvent = eventService.getSuccessor(model.getObject());
                if (null != nextEvent) {
                    ParticipateSession.get().setEvent(nextEvent.getEvent());
                    send(getWebPage(), Broadcast.BREADTH, new AjaxUpdateEvent(target));
                    model.setObject(nextEvent);
                    target.add(form);
                }
            }
        };
        nextEventBtn.setOutputMarkupPlaceholderTag(true);
        nextEventBtn.setSize(Buttons.Size.Small);
        nextEventBtn.setIconType(FontAwesome5IconType.caret_right_s);
        wmc.add(nextEventBtn);

        wmc.add(new Label("acceptedCount"));
        wmc.add(new Label("declinedCount"));
        wmc.add(new Label("pendingCount"));
        wmc.add(new Label("sopranoCount"));
        wmc.add(new Label("altoCount"));
        wmc.add(new Label("tenorCount"));
        wmc.add(new Label("bassCount"));
        wmc.add(new Label("accommodationCount"));
        wmc.add(new Label("cateringCount"));
        wmc.add(new Label("sopranos"));
        wmc.add(new Label("altos"));
        wmc.add(new Label("tenors"));
        wmc.add(new Label("basses"));
        wmc.add(new Label("declined"));
        wmc.add(new Label("carCount"));
        wmc.add(new Label("carSeatCount"));
        wmc.add(new Label("accommodationSingerCount", new PropertyModel<>(model.getObject(), "acceptedCount")));
        wmc.add(new Label("cateringSingerCount", new PropertyModel<>(model.getObject(), "acceptedCount")));
        wmc.add(new Label("carSingerCount", new PropertyModel<>(model.getObject(), "acceptedCount")));

        // Unterer Bereich
        IModel<ParticipantFilter> filterModel = new CompoundPropertyModel<>(new ParticipantFilter());

        EventSummaryListPanel listPanel = new EventSummaryListPanel("listPanel", model.map(EventDetails::getEvent), filterModel, editable);
        listPanel.add(new UpdateOnEventBehavior<>(ParticipantFilterIntent.class));
        listPanel.add(new UpdateOnEventBehavior<>(ParticipantTableUpdateIntent.class));
        listPanel.setOutputMarkupId(true);
        wmc.add(listPanel);
    }

    @Override
    public void onEvent(final IEvent<?> event) {
        final Object payload = event.getPayload();
        if (payload instanceof EventSummaryUpdateEvent) {
            final EventSummaryUpdateEvent updateEvent = (EventSummaryUpdateEvent) payload;
            final EventDetails eventDetails = updateEvent.getEventDetails();
            final AjaxRequestTarget target = updateEvent.getTarget();
            model.setObject(eventDetails);
            target.add(form);
        }
    }

    @Override
    public IModel<String> getTitle() {
        return new ResourceModel("event.summary", "Event Summary");
    }
}
