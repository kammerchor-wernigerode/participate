package de.vinado.wicket.participate.ui.event;

import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconType;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome5IconType;
import de.vinado.app.participate.management.wicket.ManagementSession;
import de.vinado.app.participate.wicket.bt5.modal.Modal;
import de.vinado.wicket.bt4.modal.ConfirmationModal;
import de.vinado.wicket.bt4.modal.ModalAnchor;
import de.vinado.wicket.common.UpdateOnEventBehavior;
import de.vinado.wicket.participate.components.PersonContext;
import de.vinado.wicket.participate.components.panels.BootstrapPanel;
import de.vinado.wicket.participate.components.snackbar.Snackbar;
import de.vinado.wicket.participate.configuration.ApplicationProperties;
import de.vinado.wicket.participate.email.EmailBuilderFactory;
import de.vinado.wicket.participate.event.ui.EventSummaryPage;
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
import de.vinado.wicket.participate.ui.event.details.ParticipantDataProvider;
import de.vinado.wicket.participate.ui.event.details.ParticipantFilterIntent;
import de.vinado.wicket.participate.ui.event.details.ParticipantTableUpdateIntent;
import de.vinado.wicket.participate.ui.pages.ParticipatePage;
import org.apache.wicket.Component;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.extensions.markup.html.basic.DefaultLinkParser;
import org.apache.wicket.extensions.markup.html.basic.ILinkParser;
import org.apache.wicket.extensions.markup.html.basic.LinkParser;
import org.apache.wicket.extensions.markup.html.basic.SmartLinkMultiLineLabel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.INamedParameters.Type;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.Strings;
import org.danekja.java.util.function.serializable.SerializableConsumer;
import org.danekja.java.util.function.serializable.SerializableFunction;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class EventPanel extends BootstrapPanel<EventDetails> {

    @SpringBean
    @SuppressWarnings("unused")
    private EventService eventService;

    @SpringBean
    private EmailBuilderFactory emailBuilderFactory;

    @SpringBean
    private ApplicationProperties applicationProperties;

    private final PersonContext personContext;
    private final IModel<ParticipantFilter> filterModel;

    private final Modal modal;
    private final Form form;

    public EventPanel(String id, IModel<EventDetails> model, boolean editable, PersonContext personContext, IModel<ParticipantFilter> filterModel) {
        super(id, model);
        setOutputMarkupPlaceholderTag(true);

        this.personContext = personContext;
        this.filterModel = filterModel;

        this.modal = modal("modal");
        add(modal);

        form = new Form("form");
        add(form);

        WebMarkupContainer wmc = new WebMarkupContainer("wmc");
        wmc.setOutputMarkupId(true);
        wmc.add(new UpdateOnEventBehavior<>(ParticipantFilterIntent.class));
        wmc.add(new UpdateOnEventBehavior<>(ParticipantTableUpdateIntent.class));
        form.add(wmc);

        wmc.add(new Label("name"));
        wmc.add(new Label("eventType"));
        wmc.add(new Label("displayDate"));
        wmc.add(new Label("creationDateTimeIso").add(new RelativeTimePipe()));
        wmc.add(new Label("location"));
        wmc.add(new SmartLinkMultiLineLabel("description", model.map(EventDetails::getDescription)) {

            @Override
            protected void onConfigure() {
                super.onConfigure();
                setVisible(!Strings.isEmpty(model.getObject().getDescription()));
            }

            @Override
            protected ILinkParser getLinkParser() {
                LinkParser parser = new LinkParser();
                parser.addLinkRenderStrategy("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", DefaultLinkParser.EMAIL_RENDER_STRATEGY);
                parser.addLinkRenderStrategy("([a-zA-Z]+://[\\w\\.\\-\\:\\/~]+)[\\w\\.:\\-/?&=%,;]*", DefaultLinkParser.URL_RENDER_STRATEGY);
                return parser;
            }
        });

        ParticipantFilterPanel filterPanel = new ParticipantFilterPanel("filterPanel", filterModel) {
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
            .build();

        ParticipantTable dataTable = ParticipantTable.builder("dataTable", dataProvider())
            .personContext(personContext)
            .rowsPerPage(15)
            .columns(decoratorFactory.decorate(columns))
            .build();
        wmc.add(dataTable);
    }

    protected Modal modal(String wicketId) {
        return new Modal(wicketId);
    }

    private ParticipantDataProvider dataProvider() {
        return new ParticipantDataProvider(getModel().map(EventDetails::getEvent), eventService, filterModel, personContext);
    }

    private void edit(AjaxRequestTarget target, IModel<Participant> rowModel) {
        IModel<ParticipantDTO> model = new CompoundPropertyModel<>(new ParticipantDTO(rowModel.getObject()));

        modal
            .size(Modal.Size.LARGE)
            .title(new ResourceModel("invitation.edit", "Edit Invitation"))
            .content(id -> new EditInvitationPanel(id, model))
            .addCloseAction(new ResourceModel("cancel", "Cancel"))
            .addSubmitAction(new ResourceModel("save", "Save"), this::onSave)
            .show(target);
    }

    private void onSave(AjaxRequestTarget target) {
        Snackbar.show(target, new ResourceModel("edit.success", "The data was saved successfully"));
        send(getWebPage(), Broadcast.BREADTH, new EventTableUpdateIntent());
        send(getWebPage(), Broadcast.BREADTH, new ParticipantTableUpdateIntent());
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
        addDropdownAction(create(new ResourceModel("email.send", "Send Email"),
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
                PageParameters pageParameters = new PageParameters(getWebPage().getPageParameters());
                pageParameters.set("event", getModelObject().getId(), Type.PATH);
                return new BookmarkablePageLink<>(id, EventSummaryPage.class, pageParameters);
            }
        };
    }

    private void invite(AjaxRequestTarget target) {
        User organizer = getSession().getMetaData(ManagementSession.user);

        List<Participant> participants = eventService.getParticipants(getModelObject().getEvent(), false);
        int count = eventService.inviteParticipants(participants, organizer);

        send(getWebPage(), Broadcast.BREADTH, new ParticipantTableUpdateIntent());
        Snackbar.show(target, "Einladung wurde an "
            + count
            + (count != 1 ? " Mitglieder " : " Mitglied ")
            + "versandt.");
    }

    private void remind(AjaxRequestTarget target) {
        User organizer = getSession().getMetaData(ManagementSession.user);
        Event event = getModelObject().getEvent();
        if (!eventService.hasParticipant(event)) {
            Snackbar.show(target, "Es wurde noch niemand eingeladen!");
            return;
        }

        ModalAnchor anchor = ((ParticipatePage) getWebPage()).getModalAnchor();
        anchor.setContent(new ConfirmationModal(anchor,
            new ResourceModel("email.send.reminder.question", "Some singers have already received an invitation. Should they be remembered?")) {

            @Override
            protected void onConfirm(AjaxRequestTarget target) {
                List<Participant> participants = eventService.getParticipants(event, InvitationStatus.PENDING);
                participants.addAll(eventService.getParticipants(event, InvitationStatus.TENTATIVE));
                int count = eventService.inviteParticipants(participants, organizer);

                Snackbar.show(target, "Erinnerung wurde an "
                    + count
                    + (count != 1 ? " Mitglieder " : " Mitglied ")
                    + "versandt.");

            }
        }.title(new ResourceModel("email.send.reminder", "Send Reminder")));
        anchor.show(target);
    }

    private static SerializableFunction<String, AbstractAction> create(IModel<String> labelModel,
                                                                       IconType icon,
                                                                       SerializableFunction<String, AbstractLink> link) {
        return id -> new AbstractAction(id, labelModel, icon) {
            @Override
            protected AbstractLink link(String id) {
                return link.apply(id);
            }
        };
    }

    private AbstractLink email(String id) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("mailto:");
        populateRecipients(builder);
        populateSender(builder);

        return new ExternalLink(id, builder.toUriString());
    }

    private void populateSender(UriComponentsBuilder builder) {
        ApplicationProperties.Mail mailProperties = applicationProperties.getMail();
        Session session = getSession();
        Optional.ofNullable(session.getMetaData(ManagementSession.user))
            .map(User::getPerson)
            .map(Person::getEmail)
            .ifPresentOrElse(email -> builder.queryParam("to", email), () -> builder.queryParam("to", mailProperties.getSender()));
    }

    private void populateRecipients(UriComponentsBuilder builder) {
        String bccValue = eventService.getParticipants(getModelObject().getEvent(), InvitationStatus.ACCEPTED).stream()
            .map(Participant::getSinger)
            .map(Person::getEmail)
            .collect(Collectors.joining(","));
        builder.queryParam("bcc", bccValue);
    }

    private void edit(AjaxRequestTarget target) {
        Event event = getModelObject().getEvent();
        CompoundPropertyModel<EventDTO> model = new CompoundPropertyModel<>(new EventDTO(event));

        modal
            .size(Modal.Size.LARGE)
            .title(new ResourceModel("event.edit", "Edit Event"))
            .content(new AddEditEventPanel(modal.getContentId(), model))
            .addCloseAction(new ResourceModel("cancel", "cancel"))
            .addSubmitAction(new ResourceModel("save", "Save"), onUpdate(model))
            .show(target);
    }

    private SerializableConsumer<AjaxRequestTarget> onUpdate(IModel<EventDTO> model) {
        return target -> {
            Event event = model.map(EventDTO::getEvent).getObject();
            if (null == event || !event.isActive()) {
                getSession().setMetaData(ManagementSession.event, null);
                send(getWebPage(), Broadcast.BREADTH, new EventSelectedEvent(null));
                Snackbar.show(target, new ResourceModel("event.remove.success", "The event has been removed"));
            } else {
                EventPanel.this.setModelObject(eventService.getEventDetails(event));
                send(getWebPage(), Broadcast.BREADTH, new EventSelectedEvent(event));
                Snackbar.show(target, new ResourceModel("event.edit.success", "The event was successfully edited"));
            }
        };
    }

    @Override
    protected void onDetach() {
        filterModel.detach();
        super.onDetach();
    }
}
