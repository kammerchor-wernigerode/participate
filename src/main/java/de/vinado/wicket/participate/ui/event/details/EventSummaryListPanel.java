package de.vinado.wicket.participate.ui.event.details;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesomeIconType;
import de.vinado.wicket.participate.ParticipateSession;
import de.vinado.wicket.participate.components.TextAlign;
import de.vinado.wicket.participate.components.modals.BootstrapModal;
import de.vinado.wicket.participate.components.panels.BnBIconPanel;
import de.vinado.wicket.participate.components.panels.IconPanel;
import de.vinado.wicket.participate.components.panels.SendEmailPanel;
import de.vinado.wicket.participate.components.snackbar.Snackbar;
import de.vinado.wicket.participate.components.tables.BootstrapAjaxDataTable;
import de.vinado.wicket.participate.components.tables.columns.BootstrapAjaxLinkColumn;
import de.vinado.wicket.participate.components.tables.columns.EnumColumn;
import de.vinado.wicket.participate.events.AjaxUpdateEvent;
import de.vinado.wicket.participate.events.EventSummaryUpdateEvent;
import de.vinado.wicket.participate.events.ShowHidePropertiesEvent;
import de.vinado.wicket.participate.model.Event;
import de.vinado.wicket.participate.model.EventDetails;
import de.vinado.wicket.participate.model.InvitationStatus;
import de.vinado.wicket.participate.model.Participant;
import de.vinado.wicket.participate.model.Person;
import de.vinado.wicket.participate.model.Voice;
import de.vinado.wicket.participate.model.dtos.ParticipantDTO;
import de.vinado.wicket.participate.model.email.MailData;
import de.vinado.wicket.participate.model.filters.DetailedParticipantFilter;
import de.vinado.wicket.participate.providers.SimpleDataProvider;
import de.vinado.wicket.participate.services.EventService;
import de.vinado.wicket.participate.ui.event.EditInvitationPanel;
import de.vinado.wicket.participate.ui.pages.BasePage;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.Broadcast;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import javax.persistence.NoResultException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class EventSummaryListPanel extends Panel {

    @SuppressWarnings("unused")
    @SpringBean
    private EventService eventService;

    private boolean showAllProperties = false;

    private SimpleDataProvider<Participant, String> dataProvider;
    private BootstrapAjaxDataTable<Participant, String> dataTable;

    private final Event event = ParticipateSession.get().getEvent();

    public EventSummaryListPanel(final String id, final IModel<List<Participant>> model, final boolean editable) {
        super(id, model);

        final DetailedParticipantFilterPanel filterPanel = new DetailedParticipantFilterPanel("filterPanel",
            new LoadableDetachableModel<List<Participant>>() {
                @Override
                protected List<Participant> load() {
                    return eventService.listParticipants(ParticipateSession.get().getEvent());
                }
            }, new CompoundPropertyModel<>(new DetailedParticipantFilter()), editable) {
            @Override
            public SimpleDataProvider<Participant, ?> getDataProvider() {
                return dataProvider;
            }

            @Override
            public DataTable<Participant, ?> getDataTable() {
                return dataTable;
            }
        };
        add(filterPanel);

        dataProvider = new SimpleDataProvider<Participant, String>(model.getObject()) {
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
        columns.add(new AbstractColumn<Participant, String>(Model.of("")) {
            @Override
            public void populateItem(final Item<ICellPopulator<Participant>> cellItem, final String componentId, final IModel<Participant> rowModel) {
                cellItem.add(new BnBIconPanel(componentId, rowModel));
            }
        });
        columns.add(new AbstractColumn<Participant, String>(new ResourceModel("period", "Period")) {
            @Override
            public void populateItem(final Item<ICellPopulator<Participant>> cellItem, final String componentId, final IModel<Participant> rowModel) {
                String formattedDate = "";

                if (null != rowModel.getObject().getFromDate() && null == rowModel.getObject().getToDate()) {
                    formattedDate = "Ab " + new SimpleDateFormat("E HH:mm").format(rowModel.getObject().getFromDate());
                } else if (null != rowModel.getObject().getToDate() && null == rowModel.getObject().getFromDate()) {
                    formattedDate = "Bis " + new SimpleDateFormat("E HH:mm").format(rowModel.getObject().getToDate());
                } else if (null != rowModel.getObject().getFromDate() && null != rowModel.getObject().getToDate()) {
                    formattedDate = new SimpleDateFormat("E HH:mm").format(rowModel.getObject().getFromDate()) + " - "
                        + new SimpleDateFormat("E HH:mm").format(rowModel.getObject().getToDate());
                }

                cellItem.add(new Label(componentId, formattedDate));
            }
        });
        columns.add(new PropertyColumn<Participant, String>(new ResourceModel("comments", "Comments"), "comment") {
            @Override
            public void populateItem(final Item<ICellPopulator<Participant>> item, final String componentId, final IModel<Participant> rowModel) {
                item.add(new MultiLineLabel(componentId, getDataModel(rowModel)));
            }

            @Override
            public String getCssClass() {
                if (showAllProperties) {
                    return super.getCssClass();
                }
                return "sr-only";
            }
        });
        if (editable) {
            columns.add(new BootstrapAjaxLinkColumn<Participant, String>(FontAwesomeIconType.pencil, new ResourceModel("invitation.edit", "Edit Invitation")) {
                @Override
                public void onClick(final AjaxRequestTarget target, final IModel<Participant> rowModel) {
                    final BootstrapModal modal = ((BasePage) getWebPage()).getModal();
                    final ParticipantDTO participantDTO = new ParticipantDTO(rowModel.getObject());

                    modal.setContent(new EditInvitationPanel(modal, new CompoundPropertyModel<>(participantDTO)) {
                        @Override
                        protected void onSaveSubmit(final IModel<ParticipantDTO> savedModel, final AjaxRequestTarget target) {
                            try {
                                final Participant savedParticipant = eventService.save(savedModel.getObject());
                                final EventDetails savedEventDetails = eventService.detailsOf(savedParticipant.getEvent());

                                send(getWebPage(), Broadcast.BREADTH, new EventSummaryUpdateEvent(savedEventDetails, target));
                                dataProvider.set(eventService.listParticipants(event));
                                target.add(dataTable);
                                Snackbar.show(target, new ResourceModel("edit.success", "The data was saved successfully"));
                            } catch (NoResultException e) {
                                throw new WicketRuntimeException("Saved participant's event must not be null", e);
                            }
                        }
                    });
                    modal.show(target);

                }
            });
            columns.add(new BootstrapAjaxLinkColumn<Participant, String>(FontAwesomeIconType.envelope, new ResourceModel("email.send", "Send Email")) {
                @Override
                public void onClick(final AjaxRequestTarget target, final IModel<Participant> rowModel) {
                    final Person person = rowModel.getObject().getSinger();
                    final MailData mailData = new MailData();
                    mailData.addTo(person.getEmail(), person.getDisplayName());

                    final BootstrapModal modal = ((BasePage) getWebPage()).getModal();
                    modal.setContent(new SendEmailPanel(modal, new CompoundPropertyModel<>(mailData)));
                    modal.show(target);
                }
            });
        }

        dataTable = new BootstrapAjaxDataTable<>("dataTable", columns, dataProvider, 30);
        dataTable.hover().condensed();
        dataTable.setOutputMarkupId(true);
        add(dataTable);
    }

    @Override
    public void onEvent(final IEvent<?> event) {
        final Object payload = event.getPayload();
        if (payload instanceof ShowHidePropertiesEvent) {
            final AjaxRequestTarget target = ((ShowHidePropertiesEvent) payload).getTarget();
            showAllProperties = !showAllProperties;
            target.add(dataTable);
        }

        if (payload instanceof AjaxUpdateEvent) {
            dataProvider.set(eventService.listParticipants(ParticipateSession.get().getEvent()));
            ((AjaxUpdateEvent) payload).getTarget().add(dataTable);
        }
    }
}
