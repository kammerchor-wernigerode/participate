package de.vinado.wicket.participate.ui.event;

import de.agilecoders.wicket.core.markup.html.bootstrap.image.IconType;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesome5IconType;
import de.vinado.wicket.bt4.modal.ConfirmationModal;
import de.vinado.wicket.bt4.modal.ModalAnchor;
import de.vinado.wicket.common.UpdateOnEventBehavior;
import de.vinado.wicket.participate.ParticipateSession;
import de.vinado.wicket.participate.components.PersonContext;
import de.vinado.wicket.participate.components.panels.BootstrapPanel;
import de.vinado.wicket.participate.components.panels.SendEmailPanel;
import de.vinado.wicket.participate.components.snackbar.Snackbar;
import de.vinado.wicket.participate.configuration.ApplicationProperties;
import de.vinado.wicket.participate.email.Email;
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
import de.vinado.wicket.participate.services.PersonService;
import de.vinado.wicket.participate.ui.event.details.ParticipantDataProvider;
import de.vinado.wicket.participate.ui.event.details.ParticipantFilterIntent;
import de.vinado.wicket.participate.ui.event.details.ParticipantTableUpdateIntent;
import de.vinado.wicket.participate.ui.pages.BasePage;
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
import org.danekja.java.util.function.serializable.SerializableFunction;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @SpringBean
    private ApplicationProperties applicationProperties;

    private final PersonContext personContext;
    private final IModel<ParticipantFilter> filterModel;

    private final Form form;

    public EventPanel(final String id, final IModel<EventDetails> model, final boolean editable, PersonContext personContext, IModel<ParticipantFilter> filterModel) {
        super(id, model);
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
        wmc.add(new SmartLinkMultiLineLabel("description", model.map(EventDetails::getDescription)) {
            private static final long serialVersionUID = 2045612009711043821L;

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
        final ModalAnchor modal = ((BasePage) getWebPage()).getModalAnchor();
        modal.setContent(new EditInvitationPanel(modal, new CompoundPropertyModel<>(new ParticipantDTO(rowModel.getObject()))) {
            @Override
            protected void onSubmit(final AjaxRequestTarget target) {
                eventService.saveParticipant(getModelObject());
                Snackbar.show(target, new ResourceModel("edit.success", "The data was saved successfully"));
                send(getWebPage(), Broadcast.BREADTH, new EventTableUpdateIntent());
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

        ModalAnchor modal = ((BasePage) getWebPage()).getModalAnchor();
        modal.setContent(new SendEmailPanel(modal, new CompoundPropertyModel<>(mailData)));
        modal.show(target);
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
        final User organizer = getSession().getMetaData(ParticipateSession.user);

        final List<Participant> participants = eventService.getParticipants(getModelObject().getEvent(), false);
        final int count = eventService.inviteParticipants(participants, organizer);

        send(getWebPage(), Broadcast.BREADTH, new ParticipantTableUpdateIntent());
        Snackbar.show(target, "Einladung wurde an "
            + count
            + (count != 1 ? " Mitglieder " : " Mitglied ")
            + "versandt.");
    }

    private void remind(AjaxRequestTarget target) {
        final User organizer = getSession().getMetaData(ParticipateSession.user);
        final Event event = getModelObject().getEvent();
        if (!eventService.hasParticipant(event)) {
            Snackbar.show(target, "Es wurde noch niemand eingeladen!");
            return;
        }

        ModalAnchor anchor = ((ParticipatePage) getWebPage()).getModalAnchor();
        anchor.setContent(new ConfirmationModal(anchor,
            new ResourceModel("email.send.reminder.question", "Some singers have already received an invitation. Should they be remembered?")) {
            private static final long serialVersionUID = -5430900540362229987L;

            @Override
            protected void onConfirm(AjaxRequestTarget target) {
                final List<Participant> participants = eventService.getParticipants(event, InvitationStatus.PENDING);
                participants.addAll(eventService.getParticipants(event, InvitationStatus.TENTATIVE));
                final int count = eventService.inviteParticipants(participants, organizer);

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
        Optional.ofNullable(session.getMetaData(ParticipateSession.user))
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
        ModalAnchor modal = ((BasePage) getWebPage()).getModalAnchor();
        modal.setContent(new AddEditEventPanel(modal, new ResourceModel("event.edit", "Edit Event"),
            new CompoundPropertyModel<>(new EventDTO(event))) {
            @Override
            public void onUpdate(final Event savedEvent, final AjaxRequestTarget target) {
                EventPanel.this.setModelObject(eventService.getEventDetails(savedEvent));
                send(getWebPage(), Broadcast.BREADTH, new EventSelectedEvent(savedEvent));
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
