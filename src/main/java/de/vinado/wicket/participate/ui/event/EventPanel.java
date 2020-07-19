package de.vinado.wicket.participate.ui.event;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesomeIconType;
import de.vinado.wicket.participate.components.TextAlign;
import de.vinado.wicket.participate.components.modals.BootstrapModal;
import de.vinado.wicket.participate.components.panels.IconPanel;
import de.vinado.wicket.participate.components.panels.SendEmailPanel;
import de.vinado.wicket.participate.components.snackbar.Snackbar;
import de.vinado.wicket.participate.components.tables.BootstrapAjaxDataTable;
import de.vinado.wicket.participate.components.tables.columns.BootstrapAjaxLinkColumn;
import de.vinado.wicket.participate.components.tables.columns.EnumColumn;
import de.vinado.wicket.participate.email.Email;
import de.vinado.wicket.participate.events.AjaxUpdateEvent;
import de.vinado.wicket.participate.events.EventUpdateEvent;
import de.vinado.wicket.participate.model.Event;
import de.vinado.wicket.participate.model.EventDetails;
import de.vinado.wicket.participate.model.InvitationStatus;
import de.vinado.wicket.participate.model.Participant;
import de.vinado.wicket.participate.model.Person;
import de.vinado.wicket.participate.model.Voice;
import de.vinado.wicket.participate.model.dtos.ParticipantDTO;
import de.vinado.wicket.participate.model.filters.ParticipantFilter;
import de.vinado.wicket.participate.providers.SimpleDataProvider;
import de.vinado.wicket.participate.services.EventService;
import de.vinado.wicket.participate.ui.pages.BasePage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.extensions.breadcrumb.IBreadCrumbModel;
import org.apache.wicket.extensions.breadcrumb.panel.BreadCrumbPanel;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.Strings;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class EventPanel extends BreadCrumbPanel {

    @SpringBean
    @SuppressWarnings("unused")
    private EventService eventService;

    private IModel<EventDetails> model;

    private Form form;

    private SimpleDataProvider<Participant, String> dataProvider;
    private BootstrapAjaxDataTable<Participant, String> dataTable;

    public EventPanel(final String id, final IBreadCrumbModel breadCrumbModel, final IModel<EventDetails> model, final boolean editable) {
        super(id, breadCrumbModel, model);
        this.model = model;
        setOutputMarkupPlaceholderTag(true);

        form = new Form("form") {
            @Override
            protected void onConfigure() {
                dataProvider.set(eventService.getParticipants(model.getObject().getEvent()));
            }
        };
        add(form);

        final WebMarkupContainer wmc = new WebMarkupContainer("wmc");
        wmc.setOutputMarkupId(true);
        form.add(wmc);

        PropertyModel<EventDetails> creationDateDay = new PropertyModel<>(model, "creationDateDay");
        PropertyModel<EventDetails> offset = new PropertyModel<>(model, "offset");
        StringResourceModel creationDateText = new StringResourceModel("creationDate.text.${offset}", model)
            .setParameters(offset, creationDateDay)
            .setDefaultValue(new StringResourceModel("creationDate.text.default").setParameters(offset, creationDateDay));

        wmc.add(new Label("name"));
        wmc.add(new Label("eventType"));
        wmc.add(new Label("displayDate"));
        wmc.add(new Label("creationDate", creationDateText));
        wmc.add(new Label("location"));
        wmc.add(new MultiLineLabel("description") {
            @Override
            protected void onConfigure() {
                setVisible(!Strings.isEmpty(model.getObject().getDescription()));
            }
        });

        final ParticipantFilterPanel filterPanel = new ParticipantFilterPanel("filterPanel",
            new LoadableDetachableModel<List<Participant>>() {
                @Override
                protected List<Participant> load() {
                    return eventService.getParticipants(model.getObject().getEvent());
                }
            },
            new CompoundPropertyModel<>(new ParticipantFilter()), new PropertyModel<>(model, "event"), editable) {
            @Override
            public SimpleDataProvider<Participant, ?> getDataProvider() {
                return dataProvider;
            }

            @Override
            public DataTable<Participant, ?> getDataTable() {
                return dataTable;
            }
        };
        wmc.add(filterPanel);

        dataProvider = new SimpleDataProvider<Participant, String>() {
            @Override
            public String getDefaultSort() {
                return "invitationStatus";
            }
        };

        final List<IColumn<Participant, String>> columns = new ArrayList<>();
        columns.add(new AbstractColumn<Participant, String>(Model.of(""), "invitationStatus") {
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
        columns.add(new PropertyColumn<>(new ResourceModel("name", "Name"), "singer.sortName", "singer.sortName"));
        columns.add(new EnumColumn<Participant, String, Voice>(new ResourceModel("voice", "voice"), "singer.voice", "singer.voice"));
        if (editable) {
            columns.add(new BootstrapAjaxLinkColumn<Participant, String>(FontAwesomeIconType.pencil, new ResourceModel("invitation.edit", "Edit Invitation")) {
                @Override
                public void onClick(final AjaxRequestTarget target, final IModel<Participant> rowModel) {
                    final BootstrapModal modal = ((BasePage) getWebPage()).getModal();
                    modal.setContent(new EditInvitationPanel(modal, new CompoundPropertyModel<>(new ParticipantDTO(rowModel.getObject()))) {
                        @Override
                        protected void onSaveSubmit(final IModel<ParticipantDTO> savedModel, final AjaxRequestTarget target) {
                            model.setObject(eventService.getEventDetails(eventService.saveParticipant(savedModel.getObject()).getEvent()));
                            dataProvider.set(eventService.getParticipants(model.getObject().getEvent()));
                            Snackbar.show(target, new ResourceModel("edit.success", "The data was saved successfully"));
                            target.add(form);
                        }
                    });
                    modal.show(target);
                }
            });
            columns.add(new BootstrapAjaxLinkColumn<Participant, String>(FontAwesomeIconType.envelope, new ResourceModel("email.send", "Send Email")) {
                @Override
                public void onClick(final AjaxRequestTarget target, final IModel<Participant> rowModel) {
                    final Person person = rowModel.getObject().getSinger();
                    final Email mailData = new Email();
                    mailData.addTo(person.getEmail(), person.getDisplayName());

                    final BootstrapModal modal = ((BasePage) getWebPage()).getModal();
                    modal.setContent(new SendEmailPanel(modal, new CompoundPropertyModel<>(mailData)));
                    modal.show(target);
                }
            });
        }

        dataTable = new BootstrapAjaxDataTable<>("dataTable", columns, dataProvider, 15);
        dataTable.setOutputMarkupId(true);
        dataTable.hover().condensed();
        wmc.add(dataTable);
    }

    @Override
    public void onEvent(final IEvent<?> iEvent) {
        super.onEvent(iEvent);
        final Object payload = iEvent.getPayload();
        if (payload instanceof EventUpdateEvent) {
            final EventUpdateEvent updateEvent = (EventUpdateEvent) payload;
            final AjaxRequestTarget target = updateEvent.getTarget();
            final Event event = updateEvent.getEvent();
            model.setObject(eventService.getEventDetails(event));
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
        return new PropertyModel<>(model, "name");
    }
}
