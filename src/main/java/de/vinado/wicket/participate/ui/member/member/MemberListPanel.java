package de.vinado.wicket.participate.ui.member.member;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesomeIconType;
import de.vinado.wicket.participate.component.modal.BootstrapModal;
import de.vinado.wicket.participate.component.provider.SimpleDataProvider;
import de.vinado.wicket.participate.component.table.BootstrapAjaxDataTable;
import de.vinado.wicket.participate.component.table.column.BootstrapAjaxLinkColumn;
import de.vinado.wicket.participate.component.table.column.EnumColumn;
import de.vinado.wicket.participate.data.Member;
import de.vinado.wicket.participate.data.Voice;
import de.vinado.wicket.participate.data.dto.MemberDTO;
import de.vinado.wicket.participate.data.email.MailData;
import de.vinado.wicket.participate.data.filter.MemberFilter;
import de.vinado.wicket.participate.event.MemberUpdateEvent;
import de.vinado.wicket.participate.service.PersonService;
import de.vinado.wicket.participate.ui.event.SendEmailPanel;
import de.vinado.wicket.participate.ui.page.BasePage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public class MemberListPanel extends Panel {

    @SuppressWarnings("unused")
    @SpringBean
    private PersonService personService;

    private IModel<List<Member>> model;

    private SimpleDataProvider<Member, String> dataProvider;
    private BootstrapAjaxDataTable<Member, String> dataTable;

    public MemberListPanel(final String id, final IModel<List<Member>> model) {
        super(id, model);

        this.model = model;

        final MemberFilterPanel filterPanel = new MemberFilterPanel("filterPanel", model, new CompoundPropertyModel<>(new MemberFilter())) {
            @Override
            public SimpleDataProvider<Member, ?> getDataProvider() {
                return dataProvider;
            }

            @Override
            public DataTable<Member, ?> getDataTable() {
                return dataTable;
            }
        };
        add(filterPanel);

        dataProvider = new SimpleDataProvider<Member, String>(model.getObject()) {
            @Override
            public String getDefaultSort() {
                return "person.sortName";
            }
        };

        final List<IColumn<Member, String>> columns = new ArrayList<>();
        columns.add(new PropertyColumn<>(new ResourceModel("name", "Name"), "person.sortName", "person.sortName"));
        columns.add(new PropertyColumn<>(new ResourceModel("email", "Email"), "person.email", "person.email"));
        columns.add(new EnumColumn<Member, String, Voice>(new ResourceModel("voice", "voice"), "voice", "voice"));
        columns.add(new BootstrapAjaxLinkColumn<Member, String>(FontAwesomeIconType.pencil, new ResourceModel("member.edit", "Edit Member")) {
            @Override
            public void onClick(final AjaxRequestTarget target, final IModel<Member> rowModel) {
                final BootstrapModal modal = ((BasePage) getWebPage()).getModal();
                final Member member = rowModel.getObject();
                modal.setContent(new AddEditMemberPanel(modal, new ResourceModel("member.edit", "Edit Member"), new CompoundPropertyModel<>(new
                    MemberDTO(member, personService.getGroupList(member)))));
                modal.show(target);
            }
        });
        columns.add(new BootstrapAjaxLinkColumn<Member, String>(FontAwesomeIconType.envelope, new ResourceModel("email.send", "Send Email")) {
            @Override
            public void onClick(final AjaxRequestTarget target, final IModel<Member> rowModel) {
                final MailData mailData = new MailData();
                mailData.setRecipients(Collections.singletonList(rowModel.getObject().getPerson()));

                final BootstrapModal modal = ((BasePage) getWebPage()).getModal();
                modal.setContent(new SendEmailPanel(modal, new CompoundPropertyModel<>(mailData)));
                modal.show(target);
            }
        });

        dataTable = new BootstrapAjaxDataTable<>("dataTable", columns, dataProvider, 20);
        dataTable.setOutputMarkupId(true);
        dataTable.hover();
        dataTable.condensed();
        add(dataTable);
    }

    @Override
    public void onEvent(final IEvent<?> event) {
        super.onEvent(event);
        final Object payload = event.getPayload();
        if (payload instanceof MemberUpdateEvent) {
            final MemberUpdateEvent updateEvent = (MemberUpdateEvent) payload;
            final AjaxRequestTarget target = updateEvent.getTarget();
            model.setObject(personService.getMemberList());
            dataProvider.set(model.getObject());
            target.add(dataTable);
        }
    }
}
