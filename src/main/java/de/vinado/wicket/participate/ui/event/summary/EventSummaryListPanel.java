package de.vinado.wicket.participate.ui.event.summary;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesomeIconType;
import de.vinado.wicket.participate.ParticipateSession;
import de.vinado.wicket.participate.component.Snackbar;
import de.vinado.wicket.participate.component.TextAlign;
import de.vinado.wicket.participate.component.modal.BootstrapModal;
import de.vinado.wicket.participate.component.panel.BootstrapAjaxLinkPanel;
import de.vinado.wicket.participate.component.panel.DinnerSleepIconPanel;
import de.vinado.wicket.participate.component.panel.IconPanel;
import de.vinado.wicket.participate.component.provider.SimpleDataProvider;
import de.vinado.wicket.participate.component.table.BootstrapAjaxDataTable;
import de.vinado.wicket.participate.component.table.column.BootstrapAjaxLinkColumn;
import de.vinado.wicket.participate.component.table.column.EnumColumn;
import de.vinado.wicket.participate.data.Event;
import de.vinado.wicket.participate.data.InvitationStatus;
import de.vinado.wicket.participate.data.Participant;
import de.vinado.wicket.participate.data.Person;
import de.vinado.wicket.participate.data.Voice;
import de.vinado.wicket.participate.data.dto.ParticipantDTO;
import de.vinado.wicket.participate.data.email.MailData;
import de.vinado.wicket.participate.data.filter.DetailedMemberToEventFilter;
import de.vinado.wicket.participate.event.AjaxUpdateEvent;
import de.vinado.wicket.participate.event.EventSummaryUpdateEvent;
import de.vinado.wicket.participate.event.ShowHidePropertiesEvent;
import de.vinado.wicket.participate.service.EventService;
import de.vinado.wicket.participate.ui.event.SendEmailPanel;
import de.vinado.wicket.participate.ui.event.event.EditMemberInvitationPanel;
import de.vinado.wicket.participate.ui.page.BasePage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
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
import org.apache.wicket.util.string.Strings;

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

        final DetailedMemberToEventFilterPanel filterPanel = new DetailedMemberToEventFilterPanel("filterPanel",
            new LoadableDetachableModel<List<Participant>>() {
                @Override
                protected List<Participant> load() {
                    return eventService.getMemberToEventList(ParticipateSession.get().getEvent());
                }
            }, new CompoundPropertyModel<>(new DetailedMemberToEventFilter()), editable) {
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
        columns.add(new PropertyColumn<>(new ResourceModel("name", "Name"), "member.person.sortName", "member.person.sortName"));
        columns.add(new EnumColumn<Participant, String, Voice>(new ResourceModel("voice", "voice"), "member.voice", "member.voice"));
        columns.add(new AbstractColumn<Participant, String>(Model.of("")) {
            @Override
            public void populateItem(final Item<ICellPopulator<Participant>> cellItem, final String componentId, final IModel<Participant> rowModel) {
                cellItem.add(new DinnerSleepIconPanel(componentId, rowModel));
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
        columns.add(new AbstractColumn<Participant, String>(new ResourceModel("comments", "Comments")) {
            @Override
            public void populateItem(final Item<ICellPopulator<Participant>> cellItem, final String componentId, final IModel<Participant> rowModel) {
                final Participant modelObject = rowModel.getObject();
                cellItem.add(new MultiLineLabel(componentId, "" +
                    (Strings.isEmpty(modelObject.getComment()) ? "" : modelObject.getComment() + "\n") +
                    (Strings.isEmpty(modelObject.getNeedsDinnerComment()) ? "" : modelObject.getNeedsDinnerComment() + "\n") +
                    (Strings.isEmpty(modelObject.getNeedsPlaceToSleepComment()) ? "" : modelObject.getNeedsPlaceToSleepComment() + "\n")));
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
                    modal.setContent(new EditMemberInvitationPanel(modal,
                        new CompoundPropertyModel<>(new ParticipantDTO(rowModel.getObject()))) {
                        @Override
                        protected void onSaveSubmit(final IModel<ParticipantDTO> savedModel, final AjaxRequestTarget target) {
                            savedModel.getObject().setReviewed(false);
                            send(getWebPage(), Broadcast.BREADTH, new EventSummaryUpdateEvent(
                                eventService.getEventDetails(
                                    eventService.saveEventToMember(savedModel.getObject()).getEvent()),
                                target));
                            dataProvider.set(eventService.getMemberToEventList(event));
                            target.add(dataTable);
                            Snackbar.show(target, new ResourceModel("edit.success", "The data was saved successfully"));
                        }
                    });
                    modal.show(target);

                }
            });
            columns.add(new BootstrapAjaxLinkColumn<Participant, String>(FontAwesomeIconType.envelope, new ResourceModel("email.send", "Send Email")) {
                @Override
                public void onClick(final AjaxRequestTarget target, final IModel<Participant> rowModel) {
                    final Person person = rowModel.getObject().getMember().getPerson();
                    final MailData mailData = new MailData();
                    mailData.addTo(person.getEmail(), person.getDisplayName());

                    final BootstrapModal modal = ((BasePage) getWebPage()).getModal();
                    modal.setContent(new SendEmailPanel(modal, new CompoundPropertyModel<>(mailData)));
                    modal.show(target);
                }
            });
            columns.add(new AbstractColumn<Participant, String>(Model.of()) {
                @Override
                public void populateItem(final Item<ICellPopulator<Participant>> item, final String componentId, final IModel<Participant> rowModel) {
                    item.add(getReviewBtn(componentId, rowModel.getObject()));
                }

                @Override
                public String getCssClass() {
                    return "width-fix-30";
                }
            });
        }

        dataTable = new BootstrapAjaxDataTable<Participant, String>("dataTable", columns, dataProvider, 30) {
            @Override
            protected Item<Participant> newRowItem(String id, int index, IModel<Participant> model) {
                final Item<Participant> rowItem = super.newRowItem(id, index, model);
                if (!InvitationStatus.PENDING.equals(model.getObject().getInvitationStatus()) && model.getObject().isReviewed()) {
                    rowItem.add(new CssClassNameAppender("success"));
                }
                return rowItem;
            }
        };
        dataTable.hover().condensed();
        dataTable.setOutputMarkupId(true);
        add(dataTable);
    }

    private BootstrapAjaxLinkPanel getReviewBtn(final String id, final Participant participant) {
        final boolean reviewed = participant.isReviewed();
        final BootstrapAjaxLinkPanel reviewBtn = new BootstrapAjaxLinkPanel(id,
            Buttons.Type.Link,
            reviewed ? FontAwesomeIconType.times : FontAwesomeIconType.check, Model.of("")) {
            @Override
            public void onClick(final AjaxRequestTarget target) {
                final ParticipantDTO dto = new ParticipantDTO(participant);
                dto.setReviewed(!reviewed);
                eventService.saveEventToMember(dto);
                dataProvider.set(eventService.getMemberToEventList(participant.getEvent()));
                target.add(dataTable);
            }
        };

        if (InvitationStatus.PENDING.equals(participant.getInvitationStatus())) {
            reviewBtn.getLink().add(new AttributeAppender("disabled", "disabled"));
        }

        return reviewBtn;
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
            dataProvider.set(eventService.getMemberToEventList(ParticipateSession.get().getEvent()));
            ((AjaxUpdateEvent) payload).getTarget().add(dataTable);
        }
    }
}
