package de.vinado.wicket.participate.ui.event.details;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesomeIconType;
import de.vinado.wicket.participate.ParticipateSession;
import de.vinado.wicket.participate.components.panels.BootstrapPanel;
import de.vinado.wicket.participate.events.AjaxUpdateEvent;
import de.vinado.wicket.participate.events.EventSummaryUpdateEvent;
import de.vinado.wicket.participate.events.ShowHidePropertiesEvent;
import de.vinado.wicket.participate.model.EventDetails;
import de.vinado.wicket.participate.model.Participant;
import de.vinado.wicket.participate.services.EventService;
import de.vinado.wicket.participate.ui.event.EventsPage;
import lombok.extern.slf4j.Slf4j;
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

import javax.persistence.NoResultException;
import java.util.List;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
@Slf4j
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
        final BootstrapAjaxLink previousEventBtn = new BootstrapAjaxLink("previousEventBtn", Buttons.Type.Link) {
            @Override
            public void onClick(final AjaxRequestTarget target) {
                try {
                    final EventDetails previousEvent = eventService.getPredecessor(model.getObject());
                    ParticipateSession.get().setEvent(previousEvent);
                    send(getWebPage(), Broadcast.BREADTH, new AjaxUpdateEvent(target));
                    model.setObject(previousEvent);
                    target.add(form);
                } catch (NoResultException e) {
                    log.debug("Could not find predecessor of event /w name={}", model.getObject().getName());
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
                setResponsePage(EventsPage.class);
            }
        };
        backBtn.setLabel(new ResourceModel("show.event.overview", "Show Event Overview"));
        backBtn.setSize(Buttons.Size.Small);
        backBtn.setIconType(FontAwesomeIconType.calendar);
        wmc.add(backBtn);

        final BootstrapAjaxLink nextEventBtn = new BootstrapAjaxLink("nextEventBtn", Buttons.Type.Link) {
            @Override
            public void onClick(final AjaxRequestTarget target) {
                try {
                    final EventDetails nextEvent = eventService.getSuccessor(model.getObject());
                    ParticipateSession.get().setEvent(nextEvent);
                    send(getWebPage(), Broadcast.BREADTH, new AjaxUpdateEvent(target));
                    model.setObject(nextEvent);
                    target.add(form);
                } catch (NoResultException e) {
                    log.debug("Could not find successor of event /w name={}", model.getObject().getName());
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
        wmc.add(new Label("accommodationCount"));
        wmc.add(new Label("cateringCount"));
        wmc.add(new Label("sopranos"));
        wmc.add(new Label("altos"));
        wmc.add(new Label("tenors"));
        wmc.add(new Label("basses"));
        wmc.add(new Label("declined"));
        wmc.add(new Label("accommodationSingerCount", new PropertyModel<>(model.getObject(), "acceptedCount")));
        wmc.add(new Label("cateringSingerCount", new PropertyModel<>(model.getObject(), "acceptedCount")));

        // Unterer Bereich
        final BootstrapPanel<List<Participant>> listPanel = new BootstrapPanel<List<Participant>>("listPanel",
            new LoadableDetachableModel<List<Participant>>() {
                @Override
                protected List<Participant> load() {
                    return eventService.getParticipants(model.getObject());
                }
            }, new PropertyModel<>(model, "name")) {
            @Override
            protected Panel newBodyPanel(final String id, final IModel<List<Participant>> model) {
                return new EventSummaryListPanel(id, model, editable);
            }

            @Override
            protected AbstractLink newDefaultBtn(final String id, final IModel<List<Participant>> model) {
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
