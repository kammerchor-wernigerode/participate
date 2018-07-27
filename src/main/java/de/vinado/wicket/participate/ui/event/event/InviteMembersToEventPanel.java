package de.vinado.wicket.participate.ui.event.event;

import de.vinado.wicket.participate.component.modal.BootstrapModal;
import de.vinado.wicket.participate.component.modal.BootstrapModalPanel;
import de.vinado.wicket.participate.component.provider.SimpleDataProvider;
import de.vinado.wicket.participate.component.table.BootstrapAjaxDataTable;
import de.vinado.wicket.participate.data.Event;
import de.vinado.wicket.participate.data.Participant;
import de.vinado.wicket.participate.service.EventService;
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

    /**
     * @param modal {@link BootstrapModal}
     * @param model Model
     */
    public InviteMembersToEventPanel(final BootstrapModal modal, final IModel<Event> model) {
        super(modal, new ResourceModel("members.invite", "Invite Members"), model);
        setModalSize(ModalSize.Large);

        final SimpleDataProvider<Participant, String> dataProvider = new SimpleDataProvider<Participant, String>(eventService.getParticipants(model.getObject())) {
            @Override
            public String getDefaultSort() {
                return "invitationStatus";
            }
        };

        final List<IColumn<Participant, String>> columns = new ArrayList<>();
        columns.add(new AbstractColumn<Participant, String>(Model.of(""), "id") {
            @Override
            public void populateItem(final Item<ICellPopulator<Participant>> cell, final String componentId, final IModel<Participant> rowModel) {
//                cell.add(new CheckboxPanel(componentId, Model.of(rowModel.getObject().getInvitationStatus().isDefault())));
            }
        });
        columns.add(new PropertyColumn<>(new ResourceModel("name", "Name"), "member.displayName", "member.displayName"));
        columns.add(new PropertyColumn<>(new ResourceModel("voice", "Voice"), "member.voice.sortOrder", "member.voice.name"));

        final BootstrapAjaxDataTable<Participant, String> dataTable = new BootstrapAjaxDataTable<>("dataTable", columns, dataProvider, 30);
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
