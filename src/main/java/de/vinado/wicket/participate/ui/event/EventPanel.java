package de.vinado.wicket.participate.ui.event;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome5IconType;
import de.vinado.wicket.common.UpdateOnEventBehavior;
import de.vinado.wicket.participate.ParticipateSession;
import de.vinado.wicket.participate.components.PersonContext;
import de.vinado.wicket.participate.components.modals.BootstrapModal;
import de.vinado.wicket.participate.components.modals.BootstrapModalConfirmationPanel;
import de.vinado.wicket.participate.components.panels.BootstrapPanel;
import de.vinado.wicket.participate.components.panels.SendEmailPanel;
import de.vinado.wicket.participate.components.snackbar.Snackbar;
import de.vinado.wicket.participate.email.Email;
import de.vinado.wicket.participate.email.EmailBuilderFactory;
import de.vinado.wicket.participate.events.AjaxUpdateEvent;
import de.vinado.wicket.participate.events.EventUpdateEvent;
import de.vinado.wicket.participate.model.Event;
import de.vinado.wicket.participate.model.EventDetails;
import de.vinado.wicket.participate.model.InvitationStatus;
import de.vinado.wicket.participate.model.Participant;
import de.vinado.wicket.participate.model.Person;
import de.vinado.wicket.participate.model.User;
import de.vinado.wicket.participate.model.dtos.EventDTO;
import de.vinado.wicket.participate.model.dtos.ParticipantDTO;
import de.vinado.wicket.participate.model.filters.ParticipantFilter;
import de.vinado.wicket.participate.services.EventService;
import de.vinado.wicket.participate.services.PersonService;
import de.vinado.wicket.participate.ui.event.details.EventSummaryPanel;
import de.vinado.wicket.participate.ui.event.details.ParticipantDataProvider;
import de.vinado.wicket.participate.ui.event.details.ParticipantFilterIntent;
import de.vinado.wicket.participate.ui.event.details.ParticipantTableUpdateIntent;
import de.vinado.wicket.participate.ui.pages.BasePage;
import de.vinado.wicket.participate.ui.pages.ParticipatePage;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanelLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.Strings;

import java.util.Date;
import java.util.List;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class EventPanel extends BootstrapPanel<EventDetails> {

    @SpringBean
    @SuppressWarnings("unused")
    private EventService eventService;

    @SpringBean
    private EmailBuilderFactory emailBuilderFactory;

    @SpringBean
    private PersonService personService;

    private final PersonContext personContext;
    private final IModel<ParticipantFilter> filterModel;
    private final IBreadCrumbModel breadCrumbModel;

    private final Form form;

    public EventPanel(final String id, final IBreadCrumbModel breadCrumbModel, final IModel<EventDetails> model, final boolean editable, PersonContext personContext, IModel<ParticipantFilter> filterModel) {
        super(id, model);
        this.breadCrumbModel = breadCrumbModel;
        setOutputMarkupPlaceholderTag(true);

        this.personContext = personContext;
        this.filterModel = filterModel;

        form = new Form("form");
        add(form);

        final WebMarkupContainer wmc = new WebMarkupContainer("wmc");
        wmc.setOutputMarkupId(true);
        wmc.add(new UpdateOnEventBehavior<>(ParticipantFilterIntent.class));
        wmc.add(new UpdateOnEventBehavior<>(ParticipantTableUpdateIntent.class));
        form.add(wmc);

        wmc.add(new Label("name"));
        wmc.add(new Label("eventType"));
        wmc.add(new Label("displayDate"));
        wmc.add(new Label("creationDateTimeIso").add(new RelativeTimePipe()));
        wmc.add(new Label("location"));
        wmc.add(new MultiLineLabel("description") {
            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisible(!Strings.isEmpty(model.getObject().getDescription()));
            }
        });

        final ParticipantFilterPanel filterPanel = new ParticipantFilterPanel("filterPanel", filterModel) {
            @Override
            protected Component getScope() {
                return wmc;
            }
        };
        wmc.add(filterPanel);

        BasicParticipantColumnPreset columns = new BasicParticipantColumnPreset();
        InteractiveColumnPresetDecoratorFactory decoratorFactory = InteractiveColumnPresetDecoratorFactory.builder()
            .visible(editable)
            .onEdit(EventPanel.this::edit)
            .onEmail(EventPanel.this::email)
            .build();

        ParticipantTable dataTable = ParticipantTable.builder("dataTable", dataProvider())
            .personContext(personContext)
            .rowsPerPage(15)
            .columns(decoratorFactory.decorate(columns))
            .build();
        wmc.add(dataTable);
    }

    private ParticipantDataProvider dataProvider() {
        return new ParticipantDataProvider(getModel().map(EventDetails::getEvent), eventService, filterModel, personContext);
    }

    private void edit(AjaxRequestTarget target, IModel<Participant> rowModel) {
        final BootstrapModal modal = ((BasePage) getWebPage()).getModal();
        modal.setContent(new EditInvitationPanel(modal, new CompoundPropertyModel<>(new ParticipantDTO(rowModel.getObject()))) {
            @Override
            protected void onSaveSubmit(final IModel<ParticipantDTO> savedModel, final AjaxRequestTarget target) {
                eventService.saveParticipant(savedModel.getObject());
                Snackbar.show(target, new ResourceModel("edit.success", "The data was saved successfully"));
                send(getWebPage(), Broadcast.BREADTH, new ParticipantTableUpdateIntent());
            }
        });
        modal.show(target);
    }

    private void email(AjaxRequestTarget target, IModel<Participant> rowModel) {
        final Person person = rowModel.getObject().getSinger();
        Email mailData = emailBuilderFactory.create()
            .to(person)
            .build();

        final BootstrapModal modal = ((BasePage) getWebPage()).getModal();
        modal.setContent(new SendEmailPanel(modal, new CompoundPropertyModel<>(mailData)));
        modal.show(target);
    }

    @Override
    public void onEvent(final IEvent<?> iEvent) {
        super.onEvent(iEvent);
        final Object payload = iEvent.getPayload();
        if (payload instanceof EventUpdateEvent) {
            final EventUpdateEvent updateEvent = (EventUpdateEvent) payload;
            final AjaxRequestTarget target = updateEvent.getTarget();
            final Event event = updateEvent.getEvent();
            setModelObject(eventService.getEventDetails(event));
            target.add(form);
        }

        if (payload instanceof AjaxUpdateEvent) {
            final AjaxUpdateEvent event = (AjaxUpdateEvent) payload;
            final AjaxRequestTarget target = event.getTarget();
            target.add(form);
        }
    }

    @Override
    protected IModel<String> titleModel() {
        return new ResourceModel("event", "Event");
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        addQuickAccessAction(this::summary);
        addQuickAccessAction(AjaxAction.create(new ResourceModel("email.send.invitation", "Send Invitation"),
            FontAwesome5IconType.envelope_square_s,
            this::invite));
        addDropdownAction(AjaxAction.create(new ResourceModel("email.send.reminder", "Send Reminder"),
            FontAwesome5IconType.exclamation_s,
            this::remind));
        addDropdownAction(AjaxAction.create(new ResourceModel("email.send", "Send Email"),
            FontAwesome5IconType.envelope_s,
            this::email));
        addDropdownAction(AjaxAction.create(new ResourceModel("event.edit", "Edit Event"),
            FontAwesome5IconType.pencil_alt_s,
            this::edit));
    }

    private AbstractAction summary(String id) {
        return new AbstractAction(id, new ResourceModel("show.event.summary", "Show Event Summary"), FontAwesome5IconType.check_s) {
            @Override
            protected AbstractLink link(String id) {
                return new BreadCrumbPanelLink(id, breadCrumbModel, (componentId, factoryModel) ->
                    new EventSummaryPanel(componentId, factoryModel,
                        new CompoundPropertyModel<>(eventService.getEventDetails(getModelObject().getEvent())),
                        getModel().map(EventDetails::getEndDate).map(date -> date.after(new Date())).getObject()
                    ));
            }
        };
    }

    private void invite(AjaxRequestTarget target) {
        final User organizer = ParticipateSession.get().getUser();

        final List<Participant> participants = eventService.getParticipants(getModelObject().getEvent(), false);
        final int count = eventService.inviteParticipants(participants, organizer);

        send(getWebPage(), Broadcast.BREADTH, new AjaxUpdateEvent(target));
        Snackbar.show(target, "Einladung wurde an "
            + count
            + (count != 1 ? " Mitglieder " : " Mitglied ")
            + "versandt.");
    }

    private void remind(AjaxRequestTarget target) {
        final User organizer = ParticipateSession.get().getUser();
        final Event event = getModelObject().getEvent();
        if (eventService.hasParticipant(event)) {
            final BootstrapModal modal = ((ParticipatePage) getWebPage()).getModal();
            modal.setContent(new BootstrapModalConfirmationPanel(modal,
                new ResourceModel("email.send.reminder", "Send Reminder"),
                new ResourceModel("email.send.reminder.question", "Some singers have already received an invitation. Should they be remembered?")) {
                @Override
                protected void onConfirm(AjaxRequestTarget target) {
                    final List<Participant> participants = eventService.getParticipants(event, InvitationStatus.PENDING);

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

    private void email(AjaxRequestTarget target) {
        Email mailData = emailBuilderFactory.create()
            .toPeople(personService.getSingers(getModelObject().getEvent()))
            .build();

        final BootstrapModal modal = ((BasePage) getWebPage()).getModal();
        modal.setContent(new SendEmailPanel(modal, new CompoundPropertyModel<>(mailData)));
        modal.show(target);
    }

    private void edit(AjaxRequestTarget target) {
        Event event = getModelObject().getEvent();
        final BootstrapModal modal = ((BasePage) getWebPage()).getModal();
        modal.setContent(new AddEditEventPanel(modal, new ResourceModel("event.edit", "Edit Event"),
            new CompoundPropertyModel<>(new EventDTO(event))) {
            @Override
            public void onUpdate(final Event savedEvent, final AjaxRequestTarget target) {
                EventPanel.this.setModelObject(eventService.getEventDetails(savedEvent));
                ParticipateSession.get().setEvent(event);
                send(getPage(), Broadcast.BREADTH, new AjaxUpdateEvent(target));
                Snackbar.show(target, new ResourceModel("event.edit.success", "The event was successfully edited"));
            }
        });
        modal.show(target);
    }

    @Override
    protected void onDetach() {
        filterModel.detach();
        super.onDetach();
    }
}
