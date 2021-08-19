package de.vinado.wicket.participate.ui.event;

import de.agilecoders.wicket.core.markup.html.bootstrap.navigation.Breadcrumb;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesomeIconType;
import de.vinado.wicket.participate.ParticipateSession;
import de.vinado.wicket.participate.components.modals.BootstrapModal;
import de.vinado.wicket.participate.components.modals.BootstrapModalConfirmationPanel;
import de.vinado.wicket.participate.components.panels.BootstrapPanel;
import de.vinado.wicket.participate.components.panels.SendEmailPanel;
import de.vinado.wicket.participate.components.snackbar.Snackbar;
import de.vinado.wicket.participate.email.Email;
import de.vinado.wicket.participate.events.AjaxUpdateEvent;
import de.vinado.wicket.participate.events.EventUpdateEvent;
import de.vinado.wicket.participate.events.RemoveEventUpdateEvent;
import de.vinado.wicket.participate.model.Event;
import de.vinado.wicket.participate.model.EventDetails;
import de.vinado.wicket.participate.model.InvitationStatus;
import de.vinado.wicket.participate.model.Participant;
import de.vinado.wicket.participate.model.User;
import de.vinado.wicket.participate.model.dtos.EventDTO;
import de.vinado.wicket.participate.services.EventService;
import de.vinado.wicket.participate.services.PersonService;
import de.vinado.wicket.participate.ui.event.details.EventSummaryPanel;
import de.vinado.wicket.participate.ui.pages.BasePage;
import de.vinado.wicket.participate.ui.pages.ParticipatePage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanel;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanelLink;
import org.apache.wicket.extensions.breadcrumb.panel.IBreadCrumbPanelFactory;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.Date;
import java.util.List;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class EventMasterPanel extends BreadCrumbPanel {

    @SpringBean
    @SuppressWarnings("unused")
    private EventService eventService;

    @SpringBean
    @SuppressWarnings("unused")
    private PersonService personService;

    private BootstrapPanel<List<EventDetails>> eventListPanel;
    private BootstrapPanel<EventDetails> eventPanel;

    public EventMasterPanel(final String id, final IBreadCrumbModel breadCrumbModel) {
        super(id, breadCrumbModel);

        ((Breadcrumb) getBreadCrumbModel()).setVisible(false);

        eventListPanel = new BootstrapPanel<List<EventDetails>>("events", new CompoundPropertyModel<>(eventService.getUpcomingEventDetails()), new ResourceModel("overview", "Overview")) {
            @Override
            protected Panel newBodyPanel(final String id, final IModel<List<EventDetails>> model) {
                return new EventsPanel(id, model);
            }

            @Override
            protected AbstractLink newDefaultBtn(final String id, final IModel<List<EventDetails>> model) {
                setDefaultBtnLabelModel(new ResourceModel("event.add", "Add Event"));
                setDefaultBtnIcon(FontAwesomeIconType.plus);
                return new AjaxLink(id) {
                    @Override
                    public void onClick(final AjaxRequestTarget target) {
                        final BootstrapModal modal = ((BasePage) getWebPage()).getModal();
                        modal.setContent(new AddEditEventPanel(modal, new ResourceModel("event.add", "Add Event"), new CompoundPropertyModel<>(new EventDTO())) {
                            @Override
                            public void onUpdate(final Event savedEvent, final AjaxRequestTarget target) {
                                ParticipateSession.get().setEvent(savedEvent);
                                send(getPage(), Broadcast.BREADTH, new EventUpdateEvent(savedEvent, target));
                                send(getPage(), Broadcast.BREADTH, new AjaxUpdateEvent(target));
                                Snackbar.show(target, new ResourceModel("event.add.success", "A new event has been added"));
                                target.add(eventPanel);
                            }
                        });
                        modal.show(target);
                    }
                };
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

        eventPanel = new BootstrapPanel<EventDetails>("event", new CompoundPropertyModel<>(eventView),
            new ResourceModel("event", "Event")) {
            @Override
            protected void onConfigure() {
                setVisible(eventService.hasUpcomingEvents());
            }

            @Override
            protected Panel newBodyPanel(final String id, final IModel<EventDetails> model) {
                return new EventPanel(id, breadCrumbModel, model, true);
            }

            @Override
            protected AbstractLink newDefaultBtn(final String id, final IModel<EventDetails> model) {
                setDefaultBtnIcon(FontAwesomeIconType.check);
                setDefaultBtnLabelModel(new ResourceModel("show.event.summary", "Show Event Summary"));
                return new BreadCrumbPanelLink(id, breadCrumbModel, (IBreadCrumbPanelFactory) (componentId, breadCrumbModel1)
                    -> new EventSummaryPanel(componentId, breadCrumbModel1,
                    new CompoundPropertyModel<>(eventService.getEventDetails(model.getObject().getEvent())),
                    model.getObject().getEndDate().after(new Date())));
            }

            @Override
            protected RepeatingView newDropDownMenu(final String id, final IModel<EventDetails> model) {
                final Date endDate = model.getObject().getEndDate();
                if (null != endDate && endDate.before(new Date())) {
                    return super.newDropDownMenu(id, model);
                }

                final User organizer = ParticipateSession.get().getUser();
                final RepeatingView dropDownMenu = super.newDropDownMenu(id, model);
                dropDownMenu.add(new DropDownItem(dropDownMenu.newChildId(), new ResourceModel("email.send.invitation", "Send Invitation"),
                    FontAwesomeIconType.envelope_square) {
                    @Override
                    protected void onClick(final AjaxRequestTarget target) {
                        final List<Participant> participants = eventService.getParticipants(model.getObject().getEvent(), false);

                        final int count = eventService.inviteParticipants(participants, organizer);

                        send(getWebPage(), Broadcast.BREADTH, new AjaxUpdateEvent(target));
                        Snackbar.show(target, "Einladung wurde an "
                            + count
                            + (count != 1 ? " Mitglieder " : " Mitglied ")
                            + "versandt.");
                    }
                });
                dropDownMenu.add(new DropDownItem(dropDownMenu.newChildId(), new ResourceModel("email.send.reminder", "Send Reminder"),
                    FontAwesomeIconType.exclamation) {
                    @Override
                    protected void onClick(AjaxRequestTarget target) {
                        final Event event = model.getObject().getEvent();
                        if (eventService.hasParticipant(event)) {
                            final BootstrapModal modal = ((ParticipatePage) getWebPage()).getModal();
                            modal.setContent(new BootstrapModalConfirmationPanel(modal,
                                new ResourceModel("email.send.reminder", "Send Reminder"),
                                new ResourceModel("email.send.reminder.question", "Some singers have already received an invitation. Should they be remembered?")) {
                                @Override
                                protected void onConfirm(AjaxRequestTarget target) {
                                    final List<Participant> participants = eventService.getParticipants(model.getObject().getEvent(), InvitationStatus.PENDING);

                                    final int count = eventService.inviteParticipants(participants, organizer);

                                    send(getWebPage(), Broadcast.BREADTH, new AjaxUpdateEvent(target));
                                    Snackbar.show(target, "Erinnerung wurde an "
                                        + count
                                        + (count != 1 ? " Mitglieder " : " Mitglied ")
                                        + "versandt.");
                                }
                            });
                            modal.show(target);
                        } else {
                            Snackbar.show(target, "Es wurde noch niemand eingeladen!");
                        }
                    }
                });
                dropDownMenu.add(new DropDownItem(dropDownMenu.newChildId(), new ResourceModel("email.send", "Send Email"),
                    FontAwesomeIconType.envelope) {
                    @Override
                    protected void onClick(final AjaxRequestTarget target) {
                        final Email mailData = new Email();
                        personService.getSingers(model.getObject().getEvent())
                            .forEach(mailData::addTo);

                        final BootstrapModal modal = ((BasePage) getWebPage()).getModal();
                        modal.setContent(new SendEmailPanel(modal, new CompoundPropertyModel<>(mailData)));
                        modal.show(target);
                    }
                });
                dropDownMenu.add(new DropDownItem(dropDownMenu.newChildId(), new ResourceModel("event.edit", "Edit Event"),
                    FontAwesomeIconType.pencil) {
                    @Override
                    protected void onClick(final AjaxRequestTarget target) {
                        final BootstrapModal modal = ((BasePage) getWebPage()).getModal();
                        modal.setContent(new AddEditEventPanel(modal, new ResourceModel("event.edit", "Edit Event"),
                            new CompoundPropertyModel<>(new EventDTO(model.getObject().getEvent()))) {
                            @Override
                            public void onUpdate(final Event savedEvent, final AjaxRequestTarget target) {
                                model.setObject(eventService.getEventDetails(savedEvent));
                                ParticipateSession.get().setEvent(model.getObject().getEvent());
                                send(getPage(), Broadcast.BREADTH, new AjaxUpdateEvent(target));
                                Snackbar.show(target, new ResourceModel("event.edit.success", "The event was successfully edited"));
                            }
                        });
                        modal.show(target);

                    }
                });
                return dropDownMenu;
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
