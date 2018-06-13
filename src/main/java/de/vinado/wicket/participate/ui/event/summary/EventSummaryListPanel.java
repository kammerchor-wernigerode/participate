package de.vinado.wicket.participate.ui.event.summary;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesomeIconType;
import de.vinado.wicket.participate.ParticipateSession;
import de.vinado.wicket.participate.component.Snackbar;
import de.vinado.wicket.participate.component.TextAlign;
import de.vinado.wicket.participate.component.modal.BootstrapModal;
import de.vinado.wicket.participate.component.panel.BootstrapAjaxLinkPanel;
import de.vinado.wicket.participate.component.panel.IconPanel;
import de.vinado.wicket.participate.component.provider.SimpleDataProvider;
import de.vinado.wicket.participate.component.table.BootstrapAjaxDataTable;
import de.vinado.wicket.participate.component.table.column.BoolIconColumn;
import de.vinado.wicket.participate.component.table.column.BootstrapAjaxLinkColumn;
import de.vinado.wicket.participate.data.Event;
import de.vinado.wicket.participate.data.InvitationStatus;
import de.vinado.wicket.participate.data.MemberToEvent;
import de.vinado.wicket.participate.data.dto.MemberToEventDTO;
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
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class EventSummaryListPanel extends Panel {

    @SuppressWarnings("unused")
    @SpringBean
    private EventService eventService;

    private boolean showAllProperties = false;

    private SimpleDataProvider<MemberToEvent, String> dataProvider;
    private BootstrapAjaxDataTable<MemberToEvent, String> dataTable;

    private final Event event = ParticipateSession.get().getEvent();

    public EventSummaryListPanel(final String id, final IModel<List<MemberToEvent>> model, final boolean editable) {
        super(id, model);

        final DetailedMemberToEventFilterPanel filterPanel = new DetailedMemberToEventFilterPanel("filterPanel",
            new LoadableDetachableModel<List<MemberToEvent>>() {
                @Override
                protected List<MemberToEvent> load() {
                    return eventService.getMemberToEventList(ParticipateSession.get().getEvent());
                }
            }, new CompoundPropertyModel<>(new DetailedMemberToEventFilter()), editable) {
            @Override
            public SimpleDataProvider<MemberToEvent, ?> getDataProvider() {
                return dataProvider;
            }

            @Override
            public DataTable<MemberToEvent, ?> getDataTable() {
                return dataTable;
            }
        };
        add(filterPanel);

        dataProvider = new SimpleDataProvider<MemberToEvent, String>(model.getObject()) {
            @Override
            public String getDefaultSort() {
                return "invitationStatus.sortOrder";
            }
        };

        final List<IColumn<MemberToEvent, String>> columns = new ArrayList<>();
        columns.add(new AbstractColumn<MemberToEvent, String>(Model.of(""), "invitationStatus.sortOrder") {
            @Override
            public void populateItem(final Item<ICellPopulator<MemberToEvent>> item, final String componentId, final IModel<MemberToEvent> rowModel) {
                final IconPanel icon = new IconPanel(componentId);
                final MemberToEvent memberToEvent = rowModel.getObject();
                final String invitationStatusIdentifier = memberToEvent.getInvitationStatus().getIdentifier();

                icon.setTextAlign(TextAlign.CENTER);
                if (InvitationStatus.ACCEPTED.equals(invitationStatusIdentifier)) {
                    icon.setType(FontAwesomeIconType.check);
                    icon.setColor(IconPanel.Color.SUCCESS);
                } else if (InvitationStatus.DECLINED.equals(invitationStatusIdentifier)) {
                    icon.setType(FontAwesomeIconType.times);
                    icon.setColor(IconPanel.Color.DANGER);
                } else if (!memberToEvent.isInvited()) {
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
        columns.add(new PropertyColumn<>(new ResourceModel("voice", "voice"), "member.voice.sortOrder", "member.voice.name"));
        columns.add(new AbstractColumn<MemberToEvent, String>(new ResourceModel("period", "Period")) {
            @Override
            public void populateItem(final Item<ICellPopulator<MemberToEvent>> cellItem, final String componentId, final IModel<MemberToEvent> rowModel) {
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
        columns.add(new BoolIconColumn<MemberToEvent, String>(new ResourceModel("needsDinner", "Food"), "needsDinner", "needsDinner") {
            @Override
            public void populateItem(final Item<ICellPopulator<MemberToEvent>> item, final String componentId, final IModel<MemberToEvent> rowModel) {
                final IconPanel iconPanel = new IconPanel(componentId, getCondition(rowModel) ? FontAwesomeIconType.check : FontAwesomeIconType.times, IconPanel.Color.DEFAULT, TextAlign.CENTER);

                if (rowModel.getObject().isNeedsDinner()) {
                    iconPanel.setColor(IconPanel.Color.SUCCESS);
                } else {
                    iconPanel.setColor(IconPanel.Color.DANGER);
                }

                item.add(iconPanel);
            }

            @Override
            public boolean getCondition(final IModel<MemberToEvent> rowModel) {
                return rowModel.getObject().isNeedsDinner();
            }
        });
        columns.add(new PropertyColumn<MemberToEvent, String>(new ResourceModel("needsDinnerComment", "Comment (food)"), "needsDinnerComment") {
            @Override
            public String getCssClass() {
                if (showAllProperties) {
                    return super.getCssClass();
                }
                return "sr-only";
            }
        });
        columns.add(new BoolIconColumn<MemberToEvent, String>(new ResourceModel("needsPlaceToSleep", "Needs place to sleep"), "needsPlaceToSleep", "needsPlaceToSleep") {
            @Override
            public void populateItem(final Item<ICellPopulator<MemberToEvent>> item, final String componentId, final IModel<MemberToEvent> rowModel) {
                final IconPanel iconPanel = new IconPanel(componentId, getCondition(rowModel) ? FontAwesomeIconType.check : FontAwesomeIconType.times, IconPanel.Color.DEFAULT, TextAlign.CENTER);

                if (rowModel.getObject().isNeedsPlaceToSleep()) {
                    iconPanel.setColor(IconPanel.Color.SUCCESS);
                } else {
                    iconPanel.setColor(IconPanel.Color.DANGER);
                }

                item.add(iconPanel);
            }

            @Override
            public boolean getCondition(final IModel<MemberToEvent> rowModel) {
                return rowModel.getObject().isNeedsPlaceToSleep();
            }
        });
        columns.add(new PropertyColumn<MemberToEvent, String>(new ResourceModel("needsPlaceToSleepComment", "Comment (place to sleep)"), "needsPlaceToSleepComment") {
            @Override
            public String getCssClass() {
                if (showAllProperties) {
                    return super.getCssClass();
                }
                return "sr-only";
            }
        });
        columns.add(new PropertyColumn<MemberToEvent, String>(new ResourceModel("comments", "Comments"), "comment") {
            @Override
            public String getCssClass() {
                if (showAllProperties) {
                    return super.getCssClass();
                }
                return "sr-only";
            }
        });
        if (editable) {
            columns.add(new BootstrapAjaxLinkColumn<MemberToEvent, String>(FontAwesomeIconType.pencil, new ResourceModel("invitation.edit", "Edit Invitation")) {
                @Override
                public void onClick(final AjaxRequestTarget target, final IModel<MemberToEvent> rowModel) {
                    final BootstrapModal modal = ((BasePage) getWebPage()).getModal();
                    modal.setContent(new EditMemberInvitationPanel(modal,
                        new CompoundPropertyModel<>(new MemberToEventDTO(rowModel.getObject()))) {
                        @Override
                        protected void onSaveSubmit(final IModel<MemberToEventDTO> savedModel, final AjaxRequestTarget target) {
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
            columns.add(new BootstrapAjaxLinkColumn<MemberToEvent, String>(FontAwesomeIconType.envelope, new ResourceModel("email.send", "Send Email")) {
                @Override
                public void onClick(final AjaxRequestTarget target, final IModel<MemberToEvent> rowModel) {
                    final MailData mailData = new MailData();
                    mailData.setRecipients(Collections.singletonList(rowModel.getObject().getMember().getPerson()));

                    final BootstrapModal modal = ((BasePage) getWebPage()).getModal();
                    modal.setContent(new SendEmailPanel(modal, new CompoundPropertyModel<>(mailData)));
                    modal.show(target);
                }
            });
            columns.add(new AbstractColumn<MemberToEvent, String>(Model.of()) {
                @Override
                public void populateItem(final Item<ICellPopulator<MemberToEvent>> item, final String componentId, final IModel<MemberToEvent> rowModel) {
                    item.add(getReviewBtn(componentId, rowModel.getObject()));
                }

                @Override
                public String getCssClass() {
                    return "width-fix-30";
                }
            });
        }

        dataTable = new BootstrapAjaxDataTable<MemberToEvent, String>("dataTable", columns, dataProvider, 30) {
            @Override
            protected Item<MemberToEvent> newRowItem(String id, int index, IModel<MemberToEvent> model) {
                final Item<MemberToEvent> rowItem = super.newRowItem(id, index, model);
                if (!"PENDING".equals(model.getObject().getInvitationStatus().getIdentifier()) && model.getObject().isReviewed()) {
                    rowItem.add(new CssClassNameAppender("success"));
                }
                return rowItem;
            }
        };
        dataTable.hover().condensed();
        dataTable.setOutputMarkupId(true);
        add(dataTable);
    }

    private BootstrapAjaxLinkPanel getReviewBtn(final String id, final MemberToEvent memberToEvent) {
        final boolean reviewed = memberToEvent.isReviewed();
        final BootstrapAjaxLinkPanel reviewBtn = new BootstrapAjaxLinkPanel(id,
            Buttons.Type.Link,
            reviewed ? FontAwesomeIconType.times : FontAwesomeIconType.check, Model.of("")) {
            @Override
            public void onClick(final AjaxRequestTarget target) {
                final MemberToEventDTO dto = new MemberToEventDTO(memberToEvent);
                dto.setReviewed(!reviewed);
                eventService.saveEventToMember(dto);
                dataProvider.set(eventService.getMemberToEventList(memberToEvent.getEvent()));
                target.add(dataTable);
            }
        };
        final boolean pending = "PENDING".equals(memberToEvent.getInvitationStatus().getIdentifier());

        if (pending) {
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
