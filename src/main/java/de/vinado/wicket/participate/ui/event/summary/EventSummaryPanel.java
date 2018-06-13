package de.vinado.wicket.participate.ui.event.summary;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesomeIconType;
import de.vinado.wicket.participate.ParticipateSession;
import de.vinado.wicket.participate.component.panel.BootstrapPanel;
import de.vinado.wicket.participate.data.MemberToEvent;
import de.vinado.wicket.participate.data.EventDetails;
import de.vinado.wicket.participate.event.AjaxUpdateEvent;
import de.vinado.wicket.participate.event.EventSummaryUpdateEvent;
import de.vinado.wicket.participate.event.ShowHidePropertiesEvent;
import de.vinado.wicket.participate.service.EventService;
import de.vinado.wicket.participate.service.ListOfValueService;
import de.vinado.wicket.participate.ui.event.EventPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class EventSummaryPanel extends BreadCrumbPanel {

    @SpringBean
    @SuppressWarnings("unused")
    private EventService eventService;

    @SpringBean
    @SuppressWarnings("unused")
    private ListOfValueService listOfValueService;

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
        final BootstrapAjaxLink previousEventBtn = new BootstrapAjaxLink("previousEventBtn", Buttons.Type.Link) {
            @Override
            public void onClick(final AjaxRequestTarget target) {
                final EventDetails previousEvent =
                        eventService.getPreviousEventDetailsView(model.getObject().getId());
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
        previousEventBtn.setIconType(FontAwesomeIconType.caret_left);
        wmc.add(previousEventBtn);

        final BootstrapAjaxLink backBtn = new BootstrapAjaxLink("backBtn", Buttons.Type.Default) {
            @Override
            public void onClick(final AjaxRequestTarget target) {
                setResponsePage(EventPage.class);
            }
        };
        backBtn.setLabel(new ResourceModel("show.event.overview", "Show Event Overview"));
        backBtn.setSize(Buttons.Size.Small);
        backBtn.setIconType(FontAwesomeIconType.calendar);
        wmc.add(backBtn);

        final BootstrapAjaxLink nextEventBtn = new BootstrapAjaxLink("nextEventBtn", Buttons.Type.Link) {
            @Override
            public void onClick(final AjaxRequestTarget target) {
                final EventDetails nextEvent = eventService.getNextEventDetailsView(model.getObject().getId());
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
        nextEventBtn.setIconType(FontAwesomeIconType.caret_right);
        wmc.add(nextEventBtn);

        wmc.add(new Label("acceptedCount"));
        wmc.add(new Label("declinedCount"));
        wmc.add(new Label("pendingCount"));
        wmc.add(new Label("sopranoCount"));
        wmc.add(new Label("altoCount"));
        wmc.add(new Label("tenorCount"));
        wmc.add(new Label("bassCount"));
        wmc.add(new Label("placeToSleepCount"));
        wmc.add(new Label("dinnerCount"));
        wmc.add(new Label("sopranosAccepted"));
        wmc.add(new Label("altosAccepted"));
        wmc.add(new Label("tenorsAccepted"));
        wmc.add(new Label("bassesAccepted"));
        wmc.add(new Label("declinedMembers"));
        wmc.add(new Label("sleepMemberCount", new PropertyModel<>(model.getObject(), "acceptedCount")));
        wmc.add(new Label("dinnerMemberCount", new PropertyModel<>(model.getObject(), "acceptedCount")));

        // Unterer Bereich
        final BootstrapPanel<List<MemberToEvent>> listPanel = new BootstrapPanel<List<MemberToEvent>>("listPanel",
                new LoadableDetachableModel<List<MemberToEvent>>() {
                    @Override
                    protected List<MemberToEvent> load() {
                        return eventService.getMemberToEventList(model.getObject().getEvent());
                    }
                }, new PropertyModel<>(model, "name")) {
            @Override
            protected Panel newBodyPanel(final String id, final IModel<List<MemberToEvent>> model) {
                return new EventSummaryListPanel(id, model, editable);
            }

            @Override
            protected AbstractLink newDefaultBtn(final String id, final IModel<List<MemberToEvent>> model) {
                setDefaultBtnLabelModel(new ResourceModel("show.all.properties", "Show all Properties"));
                setDefaultBtnIcon(FontAwesomeIconType.refresh);
                return new AjaxLink(id) {
                    @Override
                    public void onClick(final AjaxRequestTarget target) {
                        send(getWebPage(), Broadcast.BREADTH, new ShowHidePropertiesEvent(target));
                    }
                };
            }
        };
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
