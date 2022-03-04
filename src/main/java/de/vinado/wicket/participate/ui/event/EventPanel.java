package de.vinado.wicket.participate.ui.event;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesomeIconType;
import de.vinado.wicket.participate.behavoirs.UpdateOnEventBehavior;
import de.vinado.wicket.participate.components.PersonContext;
import de.vinado.wicket.participate.components.TextAlign;
import de.vinado.wicket.participate.components.modals.BootstrapModal;
import de.vinado.wicket.participate.components.panels.IconPanel;
import de.vinado.wicket.participate.components.panels.SendEmailPanel;
import de.vinado.wicket.participate.components.snackbar.Snackbar;
import de.vinado.wicket.participate.components.tables.BootstrapAjaxDataTable;
import de.vinado.wicket.participate.components.tables.columns.BootstrapAjaxLinkColumn;
import de.vinado.wicket.participate.components.tables.columns.EnumColumn;
import de.vinado.wicket.participate.email.Email;
import de.vinado.wicket.participate.email.EmailBuilderFactory;
import de.vinado.wicket.participate.events.AjaxUpdateEvent;
import de.vinado.wicket.participate.events.EventUpdateEvent;
import de.vinado.wicket.participate.model.Event;
import de.vinado.wicket.participate.model.EventDetails;
import de.vinado.wicket.participate.model.InvitationStatus;
import de.vinado.wicket.participate.model.Participant;
import de.vinado.wicket.participate.model.Person;
import de.vinado.wicket.participate.model.Singer;
import de.vinado.wicket.participate.model.Voice;
import de.vinado.wicket.participate.model.dtos.ParticipantDTO;
import de.vinado.wicket.participate.model.filters.ParticipantFilter;
import de.vinado.wicket.participate.providers.SimpleDataProvider;
import de.vinado.wicket.participate.services.EventService;
import de.vinado.wicket.participate.ui.event.details.ParticipantDataProvider;
import de.vinado.wicket.participate.ui.event.details.ParticipantFilterIntent;
import de.vinado.wicket.participate.ui.event.details.ParticipantTableUpdateIntent;
import de.vinado.wicket.participate.ui.pages.BasePage;
import org.apache.wicket.Component;
import org.apache.wicket.IGenericComponent;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanel;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.ReuseIfModelsEqualStrategy;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.Strings;
import org.danekja.java.util.function.serializable.SerializableFunction;

import java.util.ArrayList;
import java.util.List;

import static de.vinado.wicket.participate.components.Models.map;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class EventPanel extends BreadCrumbPanel implements IGenericComponent<EventDetails> {

    @SpringBean
    @SuppressWarnings("unused")
    private EventService eventService;

    @SpringBean
    private EmailBuilderFactory emailBuilderFactory;

    private final PersonContext personContext;
    private final IModel<ParticipantFilter> filterModel;

    private final Form form;

    private final SimpleDataProvider<Participant, String> dataProvider;

    public EventPanel(final String id, final IBreadCrumbModel breadCrumbModel, final IModel<EventDetails> model, final boolean editable, PersonContext personContext, IModel<ParticipantFilter> filterModel) {
        super(id, breadCrumbModel, model);
        setOutputMarkupPlaceholderTag(true);

        this.personContext = personContext;
        this.filterModel = filterModel;

        form = new Form("form") {
            @Override
            protected void onConfigure() {
                dataProvider.set(eventService.getParticipants(model.getObject().getEvent()));
            }
        };
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

        dataProvider = new SimpleDataProvider<Participant, String>() {
            @Override
            public String getDefaultSort() {
                return "invitationStatus";
            }
        };

        final List<IColumn<Participant, SerializableFunction<Participant, ?>>> columns = new ArrayList<>();
        columns.add(new AbstractColumn<Participant, SerializableFunction<Participant, ?>>(Model.of(""),
            with(Participant::getInvitationStatus).andThen(InvitationStatus::ordinal)) {
            @Override
            public void populateItem(final Item<ICellPopulator<Participant>> item, final String componentId, final IModel<Participant> rowModel) {
                final IconPanel icon = new IconPanel(componentId);
                final Participant participant = rowModel.getObject();
                final InvitationStatus invitationStatus = participant.getInvitationStatus();

                icon.setTextAlign(TextAlign.CENTER);
                if (InvitationStatus.ACCEPTED.equals(invitationStatus)) {
                    icon.setType(FontAwesomeIconType.check);
                    icon.setColor(IconPanel.Color.SUCCESS);
                } else if (InvitationStatus.DECLINED.equals(invitationStatus)) {
                    icon.setType(FontAwesomeIconType.times);
                    icon.setColor(IconPanel.Color.DANGER);
                } else if (InvitationStatus.UNINVITED.equals(invitationStatus)) {
                    icon.setType(FontAwesomeIconType.circle_thin);
                    icon.setColor(IconPanel.Color.MUTED);
                } else {
                    icon.setType(FontAwesomeIconType.circle);
                    icon.setColor(IconPanel.Color.WARNING);
                }

                item.add(icon);
            }

            @Override
            public String getCssClass() {
                return "td-with-btn-xs";
            }
        });
        columns.add(new PropertyColumn<Participant, SerializableFunction<Participant, ?>>(
            new ResourceModel("name", "Name"),
            with(Participant::getSinger).andThen(Person::getSortName),
            "singer.sortName"));
        columns.add(new EnumColumn<Participant, SerializableFunction<Participant, ?>, Voice>(
            new ResourceModel("voice", "voice"),
            with(Participant::getSinger).andThen(Singer::getVoice).andThen(nullSafe(Voice::ordinal)),
            "singer.voice"));
        if (editable) {
            columns.add(new BootstrapAjaxLinkColumn<Participant, SerializableFunction<Participant, ?>>(
                FontAwesomeIconType.pencil, new ResourceModel("invitation.edit", "Edit Invitation")) {
                @Override
                public void onClick(final AjaxRequestTarget target, final IModel<Participant> rowModel) {
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
            });
            columns.add(new BootstrapAjaxLinkColumn<Participant, SerializableFunction<Participant, ?>>(
                FontAwesomeIconType.envelope, new ResourceModel("email.send", "Send Email")) {
                @Override
                public void onClick(final AjaxRequestTarget target, final IModel<Participant> rowModel) {
                    final Person person = rowModel.getObject().getSinger();
                    Email mailData = emailBuilderFactory.create()
                        .to(person)
                        .build();

                    final BootstrapModal modal = ((BasePage) getWebPage()).getModal();
                    modal.setContent(new SendEmailPanel(modal, new CompoundPropertyModel<>(mailData)));
                    modal.show(target);
                }
            });
        }

        BootstrapAjaxDataTable<Participant, SerializableFunction<Participant, ?>> dataTable = new BootstrapAjaxDataTable<>("dataTable", columns, dataProvider(), 15);
        dataTable.setItemReuseStrategy(ReuseIfModelsEqualStrategy.getInstance());
        dataTable.setOutputMarkupId(true);
        dataTable.hover().condensed();
        wmc.add(dataTable);
    }

    private ParticipantDataProvider dataProvider() {
        ParticipantDataProvider dataProvider = new ParticipantDataProvider(map(getModel(), EventDetails::getEvent), eventService, filterModel, personContext);
        dataProvider.setSort(with(Participant::getInvitationStatus).andThen(Enum::ordinal), SortOrder.ASCENDING);
        return dataProvider;
    }

    private static <T, R> SerializableFunction<T, R> with(SerializableFunction<T, R> function) {
        return function;
    }

    private static <T, R, S extends R> SerializableFunction<T, R> nullSafe(SerializableFunction<T, S> mapper) {
        return nullable -> null == nullable ? null : mapper.apply(nullable);
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
    public IModel<String> getTitle() {
        return new PropertyModel<>(getModel(), "name");
    }

    @SuppressWarnings("unchecked")
    @Override
    public IModel<EventDetails> getModel() {
        return (IModel<EventDetails>) getDefaultModel();
    }

    @Override
    public void setModel(IModel<EventDetails> model) {
        setDefaultModel(model);
    }

    @Override
    public void setModelObject(EventDetails object) {
        setDefaultModelObject(object);
    }

    @SuppressWarnings("unchecked")
    @Override
    public EventDetails getModelObject() {
        return (EventDetails) getDefaultModelObject();
    }

    @Override
    protected void onDetach() {
        filterModel.detach();
        super.onDetach();
    }
}
