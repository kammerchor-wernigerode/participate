package de.vinado.wicket.participate.ui.event.event;

import de.vinado.wicket.participate.component.modal.BootstrapModal;
import de.vinado.wicket.participate.component.modal.BootstrapModalPanel;
import de.vinado.wicket.participate.component.provider.SimpleDataProvider;
import de.vinado.wicket.participate.component.table.BootstrapAjaxDataTable;
import de.vinado.wicket.participate.data.Event;
import de.vinado.wicket.participate.data.MemberToEvent;
import de.vinado.wicket.participate.service.EventService;
import de.vinado.wicket.participate.service.PersonService;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Vincent Nadoll (vincent.nadoll@gmail.com)
 */
public abstract class InviteMembersToEventPanel extends BootstrapModalPanel<Event> {

    @SpringBean
    @SuppressWarnings("unused")
    private EventService eventService;

    @SpringBean
    @SuppressWarnings("unused")
    private PersonService personService;

    /**
     * @param modal {@link BootstrapModal}
     * @param model Model
     */
    public InviteMembersToEventPanel(final BootstrapModal modal, final IModel<Event> model) {
        super(modal, new ResourceModel("members.invite", "Invite Members"), model);
        setModalSize(ModalSize.Large);

        final SimpleDataProvider<MemberToEvent, String> dataProvider = new SimpleDataProvider<MemberToEvent, String>(eventService.getMemberToEventList(model.getObject())) {
            @Override
            public String getDefaultSort() {
                return "invitationStatus";
            }
        };

        final List<IColumn<MemberToEvent, String>> columns = new ArrayList<>();
        columns.add(new AbstractColumn<MemberToEvent, String>(Model.of(""), "id") {
            @Override
            public void populateItem(final Item<ICellPopulator<MemberToEvent>> cell, final String componentId, final IModel<MemberToEvent> rowModel) {
//                cell.add(new CheckboxPanel(componentId, Model.of(rowModel.getObject().getInvitationStatus().isDefault())));
            }
        });
        columns.add(new PropertyColumn<>(new ResourceModel("name", "Name"), "member.displayName", "member.displayName"));
        columns.add(new PropertyColumn<>(new ResourceModel("voice", "Voice"), "member.voice.sortOrder", "member.voice.name"));

        final BootstrapAjaxDataTable<MemberToEvent, String> dataTable = new BootstrapAjaxDataTable<>("dataTable", columns, dataProvider, 30);
        dataTable.hover();
        dataTable.condensed();
        inner.add(dataTable);
    }

    @Override
    protected void onSaveSubmit(final IModel<Event> model, final AjaxRequestTarget target) {
        onUpdate(target);
    }

    public abstract void onUpdate(final AjaxRequestTarget target);
}
