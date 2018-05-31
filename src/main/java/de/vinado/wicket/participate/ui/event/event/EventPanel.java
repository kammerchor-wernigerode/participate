package de.vinado.wicket.participate.ui.event.event;

import de.agilecoders.wicket.core.markup.html.bootstrap.behavior.CssClassNameAppender;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesomeIconType;
import de.vinado.wicket.participate.component.Snackbar;
import de.vinado.wicket.participate.component.TextAlign;
import de.vinado.wicket.participate.component.modal.BootstrapModal;
import de.vinado.wicket.participate.component.panel.IconPanel;
import de.vinado.wicket.participate.component.provider.SimpleDataProvider;
import de.vinado.wicket.participate.component.table.BootstrapAjaxDataTable;
import de.vinado.wicket.participate.component.table.column.BootstrapAjaxLinkColumn;
import de.vinado.wicket.participate.data.Event;
import de.vinado.wicket.participate.data.InvitationStatus;
import de.vinado.wicket.participate.data.MemberToEvent;
import de.vinado.wicket.participate.data.dto.MemberToEventDTO;
import de.vinado.wicket.participate.data.email.MailData;
import de.vinado.wicket.participate.data.filter.MemberToEventFilter;
import de.vinado.wicket.participate.data.view.EventView;
import de.vinado.wicket.participate.event.AjaxUpdateEvent;
import de.vinado.wicket.participate.event.EventUpdateEvent;
import de.vinado.wicket.participate.event.RemoveEventUpdateEvent;
import de.vinado.wicket.participate.service.AddressService;
import de.vinado.wicket.participate.service.EmailService;
import de.vinado.wicket.participate.service.EventService;
import de.vinado.wicket.participate.service.UserService;
import de.vinado.wicket.participate.ui.event.SendEmailPanel;
import de.vinado.wicket.participate.ui.page.BasePage;
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
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.Strings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class EventPanel extends BreadCrumbPanel {

    @SpringBean
    @SuppressWarnings("unused")
    private EventService eventService;

    @SpringBean
    @SuppressWarnings("unused")
    private AddressService addressService;

    @SpringBean
    @SuppressWarnings("unused")
    private EmailService emailService;

    @SpringBean
    @SuppressWarnings("unused")
    private UserService userService;

    private IModel<EventView> model;

    private Form form;

    private SimpleDataProvider<MemberToEvent, String> dataProvider;
    private BootstrapAjaxDataTable<MemberToEvent, String> dataTable;

    public EventPanel(final String id, final IBreadCrumbModel breadCrumbModel, final IModel<EventView> model, final boolean editable) {
        super(id, breadCrumbModel, model);
        this.model = model;
        setOutputMarkupPlaceholderTag(true);

        form = new Form("form") {
            @Override
            protected void onConfigure() {
                dataProvider.set(eventService.getMemberToEventList(model.getObject().getEvent()));
            }
        };
        add(form);

        final WebMarkupContainer wmc = new WebMarkupContainer("wmc");
        wmc.setOutputMarkupId(true);
        form.add(wmc);

        wmc.add(new Label("name"));
        wmc.add(new Label("eventType"));
        wmc.add(new Label("displayDate"));
        wmc.add(new Label("location"));
        wmc.add(new MultiLineLabel("description") {
            @Override
            protected void onConfigure() {
                setVisible(!Strings.isEmpty(model.getObject().getDescription()));
            }
        });

        final Label groupLabel = new Label("cast");
        wmc.add(groupLabel);

        final MemberToEventFilterPanel filterPanel = new MemberToEventFilterPanel("filterPanel",
                new LoadableDetachableModel<List<MemberToEvent>>() {
                    @Override
                    protected List<MemberToEvent> load() {
                        return eventService.getMemberToEventList(model.getObject().getEvent());
                    }
                },
                new CompoundPropertyModel<>(new MemberToEventFilter()), new PropertyModel<>(model, "event"), editable) {
            @Override
            public SimpleDataProvider<MemberToEvent, ?> getDataProvider() {
                return dataProvider;
            }

            @Override
            public DataTable<MemberToEvent, ?> getDataTable() {
                return dataTable;
            }
        };
        wmc.add(filterPanel);

        dataProvider = new SimpleDataProvider<MemberToEvent, String>() {
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
        columns.add(new PropertyColumn<>(new ResourceModel("voiceGroup", "Voice"), "member.voice.sortOrder", "member.voice.name"));
        if (editable) {
            columns.add(new BootstrapAjaxLinkColumn<MemberToEvent, String>(FontAwesomeIconType.pencil, new ResourceModel("editInvitation", "Edit invitation")) {
                @Override
                public void onClick(final AjaxRequestTarget target, final IModel<MemberToEvent> rowModel) {
                    final BootstrapModal modal = ((BasePage) getWebPage()).getModal();
                    modal.setContent(new EditMemberInvitationPanel(modal, new CompoundPropertyModel<>(new MemberToEventDTO(rowModel.getObject()))) {
                        @Override
                        protected void onSaveSubmit(final IModel<MemberToEventDTO> savedModel, final AjaxRequestTarget target) {
                            savedModel.getObject().setReviewed(false);
                            model.setObject(eventService.getEventView(eventService.saveEventToMember(savedModel.getObject()).getEvent()));
                            dataProvider.set(eventService.getMemberToEventList(model.getObject().getEvent()));
                            Snackbar.show(target, new ResourceModel("editDataA"));
                            target.add(form);
                        }
                    });
                    modal.show(target);
                }
            });
            columns.add(new BootstrapAjaxLinkColumn<MemberToEvent, String>(FontAwesomeIconType.envelope, new ResourceModel("sendMessage", "Send message")) {
                @Override
                public void onClick(final AjaxRequestTarget target, final IModel<MemberToEvent> rowModel) {
                    final MailData mailData = new MailData();
                    mailData.setRecipients(Collections.singletonList(rowModel.getObject().getMember().getPerson()));

                    final BootstrapModal modal = ((BasePage) getWebPage()).getModal();
                    modal.setContent(new SendEmailPanel(modal, new CompoundPropertyModel<>(mailData)));
                    modal.show(target);
                }
            });
        }

        dataTable = new BootstrapAjaxDataTable<MemberToEvent, String>("dataTable", columns, dataProvider, 15) {
            @Override
            protected Item<MemberToEvent> newRowItem(final String id, final int index, final IModel<MemberToEvent> model) {
                final Item<MemberToEvent> rowItem = super.newRowItem(id, index, model);
                if (editable && !"PENDING".equals(model.getObject().getInvitationStatus().getIdentifier()) && model.getObject().isReviewed()) {
                    rowItem.add(new CssClassNameAppender("success"));
                }

                return rowItem;

            }
        };
        dataTable.setOutputMarkupId(true);
        dataTable.hover().condensed();
        wmc.add(dataTable);
    }

    protected void onRemoveEvent(final AjaxRequestTarget target) {
    }

    @Override
    public void onEvent(final IEvent<?> iEvent) {
        super.onEvent(iEvent);
        final Object payload = iEvent.getPayload();
        if (payload instanceof EventUpdateEvent) {
            final EventUpdateEvent updateEvent = (EventUpdateEvent) payload;
            final AjaxRequestTarget target = updateEvent.getTarget();
            final Event event = updateEvent.getEvent();
            model.setObject(eventService.getEventView(event));
            target.add(form);
        }

        if (payload instanceof AjaxUpdateEvent) {
            final AjaxUpdateEvent event = (AjaxUpdateEvent) payload;
            final AjaxRequestTarget target = event.getTarget();
            target.add(form);
        }

        if (payload instanceof RemoveEventUpdateEvent) {
            final RemoveEventUpdateEvent event = (RemoveEventUpdateEvent) payload;
            final AjaxRequestTarget target = event.getTarget();
            target.add(form);
            onRemoveEvent(target);
        }
    }

    @Override
    public IModel<String> getTitle() {
        return new PropertyModel<>(model, "name");
    }
}
